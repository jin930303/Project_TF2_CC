package mbc.tf2.cc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.net.URLEncoder;

@Configuration
@EnableWebSecurity
public class Security {

    private final UserDetailsService userDetailsService;

    @Autowired
    public Security(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/register",
                                "/admin/cctv_link",
                                "/admin/cctv_type",
                                "/admin/cctv_select",
                                "/uesr/updateUser",
                                "/uesr/changePassword"
                        )
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/main", "/register", "/login", "/logout", "/css/**", "/js/**", "/image/**")
                        .permitAll()
                        .requestMatchers("/user/**").hasAnyAuthority("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProcess")
                        .usernameParameter("memberId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/main")
                        .failureUrl("/")
                        .successHandler((request, response, authentication) -> {
                            System.out.println("로그인 ID : " + authentication.getName());
                            response.sendRedirect("/main");
                        })
                        .failureHandler((request, response, exception) -> {
                            String errorMessage;
                            if (exception instanceof BadCredentialsException) {
                                errorMessage = exception.getMessage();
                                System.out.println("FailureHandler Error Message: " + errorMessage);
                            } else {
                                errorMessage = "알 수 없는 오류가 발생했습니다.";
                            }
                            response.sendRedirect("/login?error=true&" +
                                    "=" + URLEncoder.encode(errorMessage, "UTF-8"));
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}

package mbc.tf2.cc.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.net.URLEncoder;

@Configuration
@EnableWebSecurity
public class Security {
    //DB 자료를 가져옴
    private UserDetailsService userDetailsService;

    @Autowired
    public Security(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    //객체생성... pw 생성
    @Bean
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        /* @formatter:off */
        http
        .csrf()
        .ignoringRequestMatchers("/cctv_select", "/cctv_link")  // 특정 경로에서만 CSRF 비활성화
        .and()
        .authorizeRequests()
        .requestMatchers("/**", "/css/**", "/js/**", "/image/**").permitAll()
        .requestMatchers("/**").hasAnyAuthority("Admin","Normal")  // 로그인 전용
        .requestMatchers("/**").hasAuthority("Admin")  // Admin 전용
        .requestMatchers("/**").hasAuthority("Normal") // Normal 전용
        .anyRequest().authenticated() // 그 외는 인증 필요
        .and()
        .formLogin()
        .permitAll()
        .loginPage("/login")
        .loginProcessingUrl("/loginProcess")
        .usernameParameter("id")
        .passwordParameter("pw")
        .defaultSuccessUrl("/")
        .failureUrl("/")
        .successHandler(new AuthenticationSuccessHandler() {
            @Override public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)throws IOException, ServletException {
                System.out.println("로그인 ID : "+authentication.getName());
                response.sendRedirect("/main");
            }
        })
        .failureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                String errorMessage;
                if (exception instanceof BadCredentialsException) {
                    errorMessage = exception.getMessage(); // Security에서 설정한 메시지
                    System.out.println("FailureHandler Error Message: " + errorMessage);
                }
                else {
                    errorMessage = "알 수 없는 오류가 발생했습니다.";
                }
                response.sendRedirect("/login?error=true&" +
                        "=" + URLEncoder.encode(errorMessage, "UTF-8"));
            }
        })
        .and()
        .logout()
        .permitAll()
        .logoutUrl("/logout")
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        .logoutSuccessUrl("/")
        .deleteCookies("JSESSIONID")
        .invalidateHttpSession(true)
        .clearAuthentication(true);

        return http.build();
        /* @formatter:on */
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
package mbc.tf2.cc.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomDetailService implements UserDetailsService {
    //회원정보를 담은 인터페이스를 상속받음

    private MemberRepository memberRepository;

    @Autowired
    public CustomDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String id) {//회원정보를 담는

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        //GrantedAuthority: 현재 사용자의 권한 admin or user를 role로 표시(role=역할)

        MemberEntity memberEntity = memberRepository.findOneById(id);
        //findOneById의 정보를 가져와서 user에 담음

        if (memberEntity.getState().equals("대기")) {
            throw new BadCredentialsException("계정이 승인 대기 중입니다.");
        }
        else if (memberEntity.getState().equals("보류")) {
            throw new BadCredentialsException("계정이 승인이 보류되었습니다.");
        }

        // 그 이후 auth에 따라 역할 설정
        if (memberEntity.getAuth() == 1) {
            grantedAuthorities.add(new SimpleGrantedAuthority("Admin"));
            return new User(memberEntity.getId(), memberEntity.getPw(), grantedAuthorities);
        } else if (memberEntity.getAuth() == 2) {
            grantedAuthorities.add(new SimpleGrantedAuthority("Normal"));
            return new User(memberEntity.getId(), memberEntity.getPw(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("can not find User : " + id);
        }
    }
}

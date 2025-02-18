package mbc.tf2.cc.service.member;

import mbc.tf2.cc.entity.member.MemberEntity;
import mbc.tf2.cc.repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
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

        Optional<MemberEntity> memberEntity = memberRepository.findByMemberId(id);
        //findByMemberId 정보를 가져와서 user에 담음

        // 그 이후 auth에 따라 역할 설정
        if (memberEntity.get().getAuth().equals("ADMIN")) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
            return new User(memberEntity.get().getMemberId(), memberEntity.get().getMemberPw(), grantedAuthorities);
        } else if (memberEntity.get().getAuth().equals("USER")) {
            grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
            return new User(memberEntity.get().getMemberId(), memberEntity.get().getMemberPw(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException("can not find User : " + id);
        }

    }
}

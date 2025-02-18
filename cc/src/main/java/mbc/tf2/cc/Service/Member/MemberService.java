package mbc.tf2.cc.Service.Member;


import mbc.tf2.cc.Entity.Member.MemberEntity;
import mbc.tf2.cc.Repository.Member.MemberRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 회원가입
    public String registerMember(MemberEntity member) {
        // 아이디 중복 검사
        if (memberRepository.findByMemberId(member.getMemberId()).isPresent()) {
            return "이미 사용 중인 아이디입니다.";
        }
        // 비밀번호 암호화
        member.encodePassword(passwordEncoder);
        memberRepository.save(member);
        return null; // 문제가 없으면 null 반환
    }

    // 로그인 검증
    public boolean validateLogin(String memberId, String password) {
        Optional<MemberEntity> memberOpt = memberRepository.findByMemberId(memberId);
        if (memberOpt.isEmpty()) {
            return false; // 아이디가 존재하지 않으면 로그인 실패
        }

        MemberEntity member = memberOpt.get();
        return passwordEncoder.matches(password, member.getMemberPw()); // 비밀번호 검증
    }
}

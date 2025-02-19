package mbc.tf2.cc.service.member;


import mbc.tf2.cc.entity.member.MemberEntity;
import mbc.tf2.cc.repository.member.MemberRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    public String registerMember(MemberEntity member) {
        // 아이디 또는 이메일 중복 체크
        if (memberRepository.findByMemberId(member.getMemberId()).isPresent()) {
            return "아이디가 이미 존재합니다.";
        }
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            return "이메일이 이미 존재합니다.";
        }

        // 비밀번호 암호화
        member.setMemberPw(passwordEncoder.encode(member.getMemberPw()));

        // 회원 저장
        memberRepository.save(member);
        return null;  // 성공적으로 가입 완료
    }

    // 로그인 검증
    public boolean validateLogin(String memberId, String password) {
        Optional<MemberEntity> memberOpt = memberRepository.findByMemberId(memberId);
        if (memberOpt.isEmpty()) {
            return false; // 아이디가 존재하지 않으면 로그인 실패
        }

        MemberEntity memberEntity = memberOpt.get();
        return passwordEncoder.matches(password, memberEntity.getMemberPw()); // 비밀번호 검증
    }
    // 세션에서 현재 로그인한 회원을 가져오는 로직 (SecurityContextHolder 사용)
    public MemberEntity getCurrentMember() {
        // 로그인된 사용자 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findByMemberId(username)
                .orElseThrow(() -> new RuntimeException("회원 정보 없음"));
    }

    // 비밀번호 변경
    public void changePassword(String oldPassword, String newPassword) {
        MemberEntity currentMember = getCurrentMember();
        if (!passwordEncoder.matches(oldPassword, currentMember.getMemberPw())) {
            throw new RuntimeException("현재 비밀번호가 틀립니다.");
        }
        currentMember.setMemberPw(passwordEncoder.encode(newPassword));
        memberRepository.save(currentMember);
    }

    // 회원 정보 업데이트
    public void updateMemberInfo(MemberEntity member) {
        MemberEntity currentMember = getCurrentMember();
        currentMember.setEmail(member.getEmail());
        currentMember.setPhone(member.getPhone());
        memberRepository.save(currentMember);
    }
}

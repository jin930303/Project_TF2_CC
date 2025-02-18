package mbc.tf2.cc.repository;

import mbc.tf2.cc.memberDTO.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberId(String memberId); // 로그인 시 사용
}

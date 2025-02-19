package mbc.tf2.cc.repository.member;

import mbc.tf2.cc.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByMemberId(String memberId); // 로그인 시 사용
    Optional<MemberEntity> findByEmail(String email);
}

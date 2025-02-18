package mbc.tf2.cc.Repository.Member;

import mbc.tf2.cc.Entity.Member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByMemberId(String memberId); // 로그인 시 사용
}

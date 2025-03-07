package mbc.tf2.cc.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq")
    @SequenceGenerator(name = "member_seq", sequenceName = "MEMBER_SEQ", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String auth = "USER";  // 기본 권한 "USER"

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String memberId;  // member_id → memberId로 수정

    @Column(nullable = false)
    private String memberPw;  // member_pw → memberPw로 수정


    private String phone;


    // 비밀번호 암호화 저장
    public void encodePassword(BCryptPasswordEncoder encoder) {
        this.memberPw = encoder.encode(this.memberPw);
    }
}


package mbc.tf2.cc.entity.cctv;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "cctv_auth")
public class CCTV_Auth_Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 기본 키 설정
    @Column(name = "cctv_auth_num")
    Long cctv_auth_num;

    @Column(name = "id")
    String id;

    @Column(name = "cctv_name")
    String cctv_name;

    @Column(name = "cctv_add_confirm")
    String cctv_add_confirm;

    @Builder
    public CCTV_Auth_Entity(Long cctv_auth_num, String id, String cctv_name, String cctv_add_confirm){
        this.cctv_auth_num = cctv_auth_num;
        this.id = id;
        this.cctv_name = cctv_name;
        this.cctv_add_confirm = cctv_add_confirm;
    }
}

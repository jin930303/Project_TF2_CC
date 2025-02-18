package mbc.tf2.cc.entity.cctv;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name = "cctv_select")
public class CCTVEntity {

    @Column(name = "cctv_location")
    String cctv_location;

    @Id
    @Column(name = "cctv_name")
    String cctv_name;

    @Column(name = "cctvurl")
    String cctvurl;

    @Builder
    public CCTVEntity(String cctv_location, String cctv_name, String cctvurl){
        this.cctv_location = cctv_location;
        this.cctv_name = cctv_name;
        this.cctvurl = cctvurl;
    }
}

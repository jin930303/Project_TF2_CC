package mbc.tf2.cc.dto.cctv;

import lombok.AllArgsConstructor;
import lombok.Data;
import mbc.tf2.cc.entity.cctv.CCTV_Auth_Entity;

@Data
@AllArgsConstructor
public class CCTV_Auth_DTO {

    private Long cctv_auth_num;
    private String id;
    private String cctv_name;
    private String cctvurl;
    private String cctv_location;
    private String cctv_add_confirm;

    public CCTV_Auth_Entity entity(){
        return CCTV_Auth_Entity.builder()
                .cctv_auth_num(cctv_auth_num)
                .id(id)
                .cctv_name(cctv_name)
                .cctv_add_confirm(cctv_add_confirm)
                .build();
    }
}

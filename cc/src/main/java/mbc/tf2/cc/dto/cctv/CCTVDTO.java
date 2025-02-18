package mbc.tf2.cc.dto.cctv;

import lombok.AllArgsConstructor;
import lombok.Data;
import mbc.tf2.cc.entity.cctv.CCTVEntity;

@Data
@AllArgsConstructor
public class CCTVDTO {
    private String cctv_location;
    private String cctv_name;
    private String cctvurl;

    public CCTVEntity entity(){
        return CCTVEntity.builder()
                .cctv_location(cctv_location)
                .cctv_name(cctv_name)
                .cctvurl(cctvurl)
                .build();
    }


}

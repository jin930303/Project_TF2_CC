package mbc.tf2.cc.Service.CCTV;

import mbc.tf2.cc.DTO.CCTV.CCTVDTO;
import mbc.tf2.cc.Entity.CCTV.CCTVEntity;

import java.util.List;

public interface CCTVService {
    List<CCTVEntity> select_cctv();

    void insert_cctv(CCTVDTO dto);

    void delete_cctv_list(String cctvName);
}

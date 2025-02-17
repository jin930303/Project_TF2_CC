package mbc.tf2.cc.Service;

import mbc.tf2.cc.DTO.CCTVDTO;
import mbc.tf2.cc.Entity.CCTVEntity;

import java.util.List;

public interface CCTVService {
    List<CCTVEntity> select_cctv();

    void insert_cctv(CCTVDTO dto);

    void delete_cctv_list(String cctvName);
}

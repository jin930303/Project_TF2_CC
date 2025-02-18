package mbc.tf2.cc.service.cctv;

import mbc.tf2.cc.dto.cctv.CCTVDTO;
import mbc.tf2.cc.entity.cctv.CCTVEntity;

import java.util.List;

public interface CCTVService {
    List<CCTVEntity> select_cctv();

    void insert_cctv(CCTVDTO dto);

    void delete_cctv_list(String cctvName);
}

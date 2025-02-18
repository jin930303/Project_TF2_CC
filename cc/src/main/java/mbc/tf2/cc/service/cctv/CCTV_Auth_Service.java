package mbc.tf2.cc.service.cctv;

import mbc.tf2.cc.dto.cctv.CCTV_Auth_DTO;
import mbc.tf2.cc.entity.cctv.CCTV_Auth_Entity;

import java.util.List;

public interface CCTV_Auth_Service {

    void insert_cctv_auth(CCTV_Auth_Entity cae);

    List<CCTV_Auth_DTO> select_user_cctv(String userId);

    void user_cctv_del(long cctvAuthNum);

    List<CCTV_Auth_DTO> select_user_cctv_all();

    void auth_update_confirm(long cctvAuthNum);

    void auth_update_wait(long cctvAuthNum);

}

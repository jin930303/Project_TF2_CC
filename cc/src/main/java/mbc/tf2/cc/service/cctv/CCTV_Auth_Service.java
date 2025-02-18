package mbc.tf2.cc.service.cctv;

import mbc.tf2.cc.entity.cctv.CCTV_Auth_Entity;

import java.util.List;

public interface CCTV_Auth_Service {

    List<CCTV_Auth_Entity> select_user_cctv(String userId);
}

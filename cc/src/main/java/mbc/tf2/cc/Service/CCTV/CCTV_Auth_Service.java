package mbc.tf2.cc.Service.CCTV;

import mbc.tf2.cc.Entity.CCTV.CCTV_Auth_Entity;

import java.util.List;

public interface CCTV_Auth_Service {

    List<CCTV_Auth_Entity> select_user_cctv(String userId);
}

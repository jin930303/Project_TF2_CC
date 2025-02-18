package mbc.tf2.cc.service.cctv;

import mbc.tf2.cc.dto.cctv.CCTV_Auth_DTO;
import mbc.tf2.cc.entity.cctv.CCTV_Auth_Entity;
import mbc.tf2.cc.repository.cctv.CCTV_Auth_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CCTV_Auth_ServiceImp implements CCTV_Auth_Service {

    @Autowired
    CCTV_Auth_Repository car;

    @Override
    public List<CCTV_Auth_DTO> select_user_cctv(String userId) {
        return car.user_cctv(userId);
    }

    @Override
    public void user_cctv_del(long cctvAuthNum) {
        car.deleteById(String.valueOf(cctvAuthNum));
    }

    @Override
    public List<CCTV_Auth_DTO> select_user_cctv_all() {
        return car.user_cctv_all();
    }

    @Override
    public void auth_update_confirm(long cctvAuthNum) {
        car.update_confirm(cctvAuthNum);
    }

    @Override
    public void auth_update_wait(long cctvAuthNum) {
        car.update_wait(cctvAuthNum);
    }

    @Override
    public void delete_cctv_list_all(String cctvName) {
        car.del_user_cctvlist(cctvName);
    }

    @Override
    public void insert_cctv_auth(CCTV_Auth_Entity dto) {
        car.save(dto);
    }
}

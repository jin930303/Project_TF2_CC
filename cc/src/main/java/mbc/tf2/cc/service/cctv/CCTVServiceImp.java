package mbc.tf2.cc.service.cctv;

import mbc.tf2.cc.dto.cctv.CCTVDTO;
import mbc.tf2.cc.entity.cctv.CCTVEntity;
import mbc.tf2.cc.repository.cctv.CCTVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CCTVServiceImp implements CCTVService {

    @Autowired
    CCTVRepository cr;

    @Override
    public List<CCTVEntity> select_cctv() {
        return cr.findAll();
    }

    @Override
    public void insert_cctv(CCTVDTO dto) {
        CCTVEntity ce = dto.entity();
        cr.save(ce);
    }

    @Override
    public void delete_cctv_list(String cctvName) {
        cr.deleteById(cctvName);
    }

}

package mbc.tf2.cc.repository.cctv;

import mbc.tf2.cc.dto.cctv.CCTV_Auth_DTO;
import mbc.tf2.cc.entity.cctv.CCTV_Auth_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CCTV_Auth_Repository extends JpaRepository<CCTV_Auth_Entity, String> {

    @Transactional
    @Query(value = "select ca.cctv_auth_num, ca.id, ca.cctv_name, cs.cctvurl, cs.cctv_location " +
            "from cctv_auth ca " +
            "join cctv_select cs on ca.cctv_name = cs.cctv_name " +
            "where ca.id = :userId", nativeQuery = true)
    List<CCTV_Auth_DTO> user_cctv(@Param("userId") String userId);

}

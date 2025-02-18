package mbc.tf2.cc.repository.cctv;

import mbc.tf2.cc.dto.cctv.CCTV_Auth_DTO;
import mbc.tf2.cc.entity.cctv.CCTV_Auth_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CCTV_Auth_Repository extends JpaRepository<CCTV_Auth_Entity, String> {

    @Modifying
    @Transactional
    @Query(value = "select ca.cctv_auth_num, ca.id, ca.cctv_name, ca.cctv_add_confirm, cs.cctv_location, cs.cctvurl " +
            "from cctv_auth ca " +
            "join cctv_select cs on ca.cctv_name = cs.cctv_name " +
            "where ca.id = :userId and ca.cctv_add_confirm = '승인'", nativeQuery = true)
    List<CCTV_Auth_DTO> user_cctv(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "select ca.cctv_auth_num, ca.id, ca.cctv_name, ca.cctv_add_confirm, cs.cctv_location, cs.cctvurl " +
            "from cctv_auth ca " +
            "join cctv_select cs on cs.cctv_name = ca.cctv_name " +
            "where ca.cctv_add_confirm in ('대기','보류')", nativeQuery = true)
    List<CCTV_Auth_DTO> user_cctv_all();

    @Modifying
    @Transactional
    @Query(value = "update cctv_auth ca set ca.cctv_add_confirm = '승인' where ca.cctv_auth_num = :cctvAuthNum", nativeQuery = true)
    void update_confirm(@Param("cctvAuthNum") long cctvAuthNum);

    @Modifying
    @Transactional
    @Query(value = "update cctv_auth ca set ca.cctv_add_confirm = '보류' where ca.cctv_auth_num = :cctvAuthNum", nativeQuery = true)
    void update_wait(@Param("cctvAuthNum") long cctvAuthNum);

    @Modifying
    @Transactional
    @Query(value = "delete from cctv_auth ca where ca.cctv_name = :cctvName", nativeQuery = true)
    void del_user_cctvlist(@Param("cctvName") String cctvName);
}

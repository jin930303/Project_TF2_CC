package mbc.tf2.cc.repository.cctv;

import mbc.tf2.cc.entity.cctv.CCTVEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCTVRepository extends JpaRepository<CCTVEntity, String> {

}

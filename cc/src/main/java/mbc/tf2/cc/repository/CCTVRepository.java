package mbc.tf2.cc.Repository;

import mbc.tf2.cc.Entity.CCTVEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CCTVRepository extends JpaRepository<CCTVEntity, String> {

}

package mbc.tf2.cc.repository;

import jakarta.transaction.Transactional;
import mbc.tf2.cc.boardDTO.BoardDTO;
import mbc.tf2.cc.boardEntity.BoardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<BoardEntity,Long> {

    @Modifying
    @Transactional
    @Query(value = "update board set confirm ='1' where board.id=:bid",nativeQuery = true)
    void update(long bid);




}

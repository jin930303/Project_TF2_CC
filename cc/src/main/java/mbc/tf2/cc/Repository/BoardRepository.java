package mbc.tf2.cc.Repository;

import jakarta.transaction.Transactional;
import mbc.tf2.cc.Entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity,Long> {

    @Modifying
    @Transactional
    @Query(value = "update board set confirm ='1' where board.id=:bid",nativeQuery = true)
    void update(long bid);

    @Modifying
    @Transactional
    @Query(value = "SELECT new mbc.tf2.cc.boardDTO.BoardDTO( " +
            "  b.id, " +
            "  FUNCTION('TO_CHAR', b.startTime, 'YY-MM-DD HH24:MI:SS'), " + // JPQL에서 TO_CHAR 사용 가능
            "  b.title, " +
            "  t.name, " +
            "  b.imgFile, " +
            "  b.confirm " +
            ") " +
            "FROM BoardEntity b " +
            "JOIN b.tag t " +
            "ORDER BY b.id DESC",nativeQuery = true)
    List<BoardEntity> findjoin();
}

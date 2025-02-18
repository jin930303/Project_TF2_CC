package mbc.tf2.cc.Repository;

import jakarta.transaction.Transactional;
import mbc.tf2.cc.Entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<mbc.tf2.cc.Entity.BoardEntity,Long> {

    @Modifying
    @Transactional
    @Query(value = "update board set confirm ='1' where board.id=:bid",nativeQuery = true)
    void update(long bid);

    @Modifying
    @Transactional
    @Query(value = """
    SELECT b.id, b.tag_id, 
    TO_CHAR(TO_DATE(b.start_time, 'YYYY-MM-DD HH24:MI:SS'), 'YY-MM-DD HH24:MI:SS') as start_time, 
    b.title, t.name as tag_name, b.img_file, b.confirm 
    FROM board b 
    JOIN tag t ON b.tag_id = t.id 
    ORDER BY b.id 
    OFFSET :offset ROWS FETCH NEXT :size ROWS ONLY
""", nativeQuery = true)
    List<BoardEntity> findjoin(@Param("size") int size, @Param("offset") int offset);


}

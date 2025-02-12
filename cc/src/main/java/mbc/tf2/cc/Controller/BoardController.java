package mbc.tf2.cc.Controller;

import mbc.tf2.cc.boardDTO.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    public ResponseEntity<List<BoardDTO>> getBoardList(){
        try {
            String sql = "SELECT b.ID, \n" +
                    "       TO_CHAR(b.START_TIME, 'YYYY-MM-DD HH24:MI:SS') AS START_TIME, \n" +
                    "       b.TITLE, \n" +
                    "       t.NAME AS TAG_NAME,  -- tag 테이블에서 name을 가져옵니다.\n" +
                    "       b.IMG_FILE \n" +
                    "FROM BOARD b\n" +
                    "JOIN TAG t ON b.TAG_ID = t.ID  -- BOARD 테이블의 TAG_ID와 TAG 테이블의 ID를 연결\n" +
                    "ORDER BY b.ID DESC";

            List<BoardDTO> boards = jdbcTemplate.query(sql, new RowMapper<BoardDTO>() {
                @Override
                public BoardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BoardDTO dto = new BoardDTO();
                    dto.setId(rs.getLong("ID"));
                    dto.setStartTime(rs.getString("START_TIME"));
                    dto.setTitle(rs.getString("TITLE"));
                    dto.setTagName(rs.getString("TAG_Name"));

                    Blob blob = rs.getBlob("IMG_FILE");
                    if (blob != null) {
                        byte[] imgBytes = blob.getBytes(1, (int) blob.length());
                        String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imgBytes); // MIME 타입 추가
                        dto.setBase64ImgFile(base64Image);
                    } else {
                        dto.setBase64ImgFile(null);
                    }
                    return dto;
                }
            });
            return ResponseEntity.ok(boards);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

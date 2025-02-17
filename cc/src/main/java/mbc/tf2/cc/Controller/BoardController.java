package mbc.tf2.cc.Controller;

import mbc.tf2.cc.boardDTO.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;


@Controller
public class BoardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/board")
    public String getBoardList(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, Model mo )
    {
        String countSql = "SELECT COUNT(*) FROM BOARD";
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        int offset = (page - 1) * size;

        String sql = "SELECT b.ID, " +
                "       TO_CHAR(TO_DATE(b.START_TIME, 'YYYY-MM-DD HH24:MI:SS'),'YYYY-MM-DD HH24:MI:SS') AS START_TIME, " +
                "       b.TITLE, " +
                "       t.NAME AS TAG_NAME, " +
                "       b.IMG_FILE " +
                "FROM BOARD b " +
                "JOIN TAG t ON b.TAG_ID = t.ID " +
                "ORDER BY b.ID DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<BoardDTO> boards = jdbcTemplate.query(sql, new Object[]{offset, size}, new RowMapper<BoardDTO>() {
            @Override
            public BoardDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                BoardDTO dto = new BoardDTO();
                dto.setId(rs.getLong("ID"));
                dto.setStartTime(rs.getString("START_TIME"));
                dto.setTitle(rs.getString("TITLE"));
                dto.setTagName(rs.getString("TAG_NAME"));

                Blob blob = rs.getBlob("IMG_FILE");
                if (blob != null) {
                    byte[] imgBytes = blob.getBytes(1, (int) blob.length());
                    String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(imgBytes);
                    dto.setBase64ImgFile(base64Image);
                } else {
                    dto.setBase64ImgFile(null);
                }
                return dto;
            }
        });

        mo.addAttribute("boards", boards);
        mo.addAttribute("currentPage", page);
        mo.addAttribute("totalPages", totalPages);
        mo.addAttribute("totalCount", totalCount);

        return "board";
    }
}

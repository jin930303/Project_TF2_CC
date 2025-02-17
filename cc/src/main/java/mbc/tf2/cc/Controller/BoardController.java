package mbc.tf2.cc.Controller;

import mbc.tf2.cc.DTO.BoardDTO;
import mbc.tf2.cc.Entity.BoardEntity;
import mbc.tf2.cc.Service.BoardService;
import mbc.tf2.cc.Repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class BoardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @GetMapping("/board")
    public String getBoardList(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, Model mo )
    {
        String countSql = "SELECT COUNT(*) FROM BOARD";
        int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        int offset = (page - 1) * size;
        int groupSize = 5;
        int startPage = ((page - 1) / groupSize) * groupSize + 1;
        int endPage = Math.min(startPage + groupSize - 1, totalPages);

        List<BoardEntity> boards = boardRepository.findjoin();

        List<BoardDTO> dtolist = boards.stream().map(board -> {
            BoardDTO dto = new BoardDTO();
            dto.setId(board.getId());
            dto.setStartTime(board.getStart_time());
            dto.setConfirm(board.getConfirm());
            dto.setTitle(board.getTitle());
            dto.setTagName(board.getTag().getName());
            Blob blob = board.getImg_file();
            if(blob !=null){
                try {
                    byte[] bytes =blob.getBytes(1,(int) blob.length());
                    String base64img = Base64.getEncoder().encodeToString(bytes);
                    dto.setBase64ImgFile(base64img);
                }
                catch (SQLException e){
                    e.printStackTrace();
                }

            }
            return dto;
    }).collect(Collectors.toList());

        mo.addAttribute("boards", boards);
        mo.addAttribute("currentPage", page);
        mo.addAttribute("totalPages", totalPages);
        mo.addAttribute("totalCount", totalCount);
        mo.addAttribute("startPage",startPage);
        mo.addAttribute("endPage",endPage);
        return "board";
    }

    @GetMapping("/confirm")
    public String confirm(@RequestParam("bid")long bid,Model mo )
    {
        boardService.confirm(bid);
        return "redirect:/board";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("bid")long bid)
    {
        boardRepository.deleteById(bid);
        return "redirect:/board";
    }


}

package mbc.tf2.cc.controller.board;

import mbc.tf2.cc.dto.board.BoardDTO;
import mbc.tf2.cc.entity.board.BoardEntity;
import mbc.tf2.cc.repository.board.BoardRepository;
import mbc.tf2.cc.service.board.BoardService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @GetMapping("/user/board")
    public String getBoardList(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size,  @RequestParam(defaultValue = "all") String status,Model mo ) {

        Page<BoardDTO> boardPage = boardService.getBoardList(page, size,status);

        int totalPages = boardPage.getTotalPages();
        int groupSize = 5;
        int startPage = ((page - 1) / groupSize) * groupSize + 1;
        int endPage = Math.min(startPage + groupSize - 1, totalPages);

        mo.addAttribute("boards", boardPage.getContent());
        mo.addAttribute("page", page);
        mo.addAttribute("totalPages", totalPages);
        mo.addAttribute("startPage",startPage);
        mo.addAttribute("size", size);
        mo.addAttribute("endPage", endPage);
        mo.addAttribute("status",status);
        return "board";
    }

    @GetMapping("/user/confirm")
    public String confirm(@RequestParam("bid")long bid,@RequestParam("page")int page)
    {
        boardService.confirm(bid);
        return "redirect:/user/board?page="+page;
    }

    @GetMapping("/user/delete")
    public String delete(@RequestParam("bid")long bid,@RequestParam("page")int page)
    {
        boardRepository.deleteById(bid);
        return "redirect:/user/board?page="+page;
    }

}

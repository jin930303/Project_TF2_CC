package mbc.tf2.cc.Controller;

import mbc.tf2.cc.DTO.BoardDTO;
import mbc.tf2.cc.Repository.BoardRepository;
import mbc.tf2.cc.Service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @GetMapping("/board")
    public String getBoardList(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, Model mo ) {

        Page<BoardDTO> boardPage = boardService.getBoardList(page, size);

        int totalPages = boardPage.getTotalPages();
        int groupSize = 5;
        int startPage = ((page - 1) / groupSize) * groupSize + 1;
        int endPage = Math.min(startPage + groupSize - 1, totalPages);

        System.out.println("현재 페이지: " + page);
        System.out.println("전체 페이지: " + totalPages);
        System.out.println("시작 페이지: " + startPage);
        System.out.println("끝 페이지: " + endPage);

        mo.addAttribute("boards", boardPage.getContent());
        mo.addAttribute("page", page);
        mo.addAttribute("totalPages", totalPages);
        mo.addAttribute("startPage",startPage);
        mo.addAttribute("size", size);
        mo.addAttribute("endPage", endPage);
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

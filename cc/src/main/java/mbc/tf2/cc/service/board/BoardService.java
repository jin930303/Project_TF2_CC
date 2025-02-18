package mbc.tf2.cc.service.board;

import mbc.tf2.cc.dto.board.BoardDTO;
import org.springframework.data.domain.Page;

public interface BoardService {


     void confirm(long bid);


     Page<BoardDTO> getBoardList(int page, int size);
}

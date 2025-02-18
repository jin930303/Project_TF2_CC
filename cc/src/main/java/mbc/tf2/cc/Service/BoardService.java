package mbc.tf2.cc.Service;

import mbc.tf2.cc.DTO.BoardDTO;
import org.springframework.data.domain.Page;

public interface BoardService {


     void confirm(long bid);


     Page<BoardDTO> getBoardList(int page, int size);
}

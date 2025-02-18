package mbc.tf2.cc.Service.Board;

import mbc.tf2.cc.DTO.BoardDTO;
import mbc.tf2.cc.Entity.BoardEntity;
import org.springframework.data.domain.Page;

public interface BoardService {


     void confirm(long bid);


     Page<BoardDTO> getBoardList(int page, int size);
}

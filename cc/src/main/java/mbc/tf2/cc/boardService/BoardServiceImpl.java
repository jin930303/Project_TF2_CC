package mbc.tf2.cc.boardService;

import mbc.tf2.cc.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    BoardRepository boardRepository;

    @Override
    public void confirm(long bid) {
        boardRepository.update(bid);
    }
}

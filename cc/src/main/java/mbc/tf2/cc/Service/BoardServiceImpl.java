package mbc.tf2.cc.Service;

import mbc.tf2.cc.Repository.BoardRepository;
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

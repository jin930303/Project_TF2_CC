package mbc.tf2.cc.Service;

import mbc.tf2.cc.DTO.Board.BoardDTO;
import mbc.tf2.cc.Entity.Board.BoardEntity;
import mbc.tf2.cc.Repository.BoardRepository;
import mbc.tf2.cc.Service.Board.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    BoardRepository boardRepository;

    @Override
    public void confirm(long bid) {
        boardRepository.update(bid);
    }

    @Override
    public Page<BoardDTO> getBoardList(int page, int size) {

        int offset = (page - 1) * size; // OFFSET 계산
        List<BoardEntity> boardEntities = boardRepository.findjoin(size, offset);

        List<BoardDTO> boardDTOs = boardEntities.stream().map(board -> {
            BoardDTO dto = new BoardDTO();
            dto.setId(board.getId());
            dto.setStartTime(board.getStartTime());
            dto.setConfirm(board.getConfirm());
            dto.setTitle(board.getTitle());
            dto.setTagName(board.getTagName().getName());
            Blob blob = board.getImg_file();
            if (blob != null) {
                try {
                    byte[] bytes = blob.getBytes(1, (int) blob.length());
                    String base64img = Base64.getEncoder().encodeToString(bytes);
                    dto.setBase64ImgFile(base64img);
                } catch ( SQLException e) {
                    e.printStackTrace();
                }
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(boardDTOs, PageRequest.of(page - 1, size), boardRepository.count());

    }
}

package mbc.tf2.cc.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardDTO {

    private long id;
    private String tagName;
    private String title;
    private String startTime;
    private String base64ImgFile;
    private String confirm;

    public BoardDTO() {}



}

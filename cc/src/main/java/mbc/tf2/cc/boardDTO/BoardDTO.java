package mbc.tf2.cc.boardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

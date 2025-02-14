package mbc.tf2.cc.boardDTO;

public class BoardDTO {

    private long id;
    private String tagName;
    private String title;
    private String startTime;
    private String base64ImgFile;


    public BoardDTO() {}


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getBase64ImgFile() {
        return base64ImgFile;
    }

    public void setBase64ImgFile(String base64ImgFile) {
        this.base64ImgFile = base64ImgFile;
    }
}

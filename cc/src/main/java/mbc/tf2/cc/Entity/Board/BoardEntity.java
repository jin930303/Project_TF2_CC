package mbc.tf2.cc.Entity.Board;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;

@Entity
@Data
@Table(name = "board")
@SequenceGenerator(name = "id_seq", sequenceName = "id_seq", allocationSize = 1, initialValue = 1000)
public class BoardEntity {
    @Id
    @Column
    @GeneratedValue(generator = "id_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "start_time",columnDefinition = "char(17)")
    private String start_time;
    private String title;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private TagEntity tag;
    private Blob img_file;

    @Column(name = "confirm", columnDefinition = "char(1)")
    private String confirm;

}

package com.hckst.respal.resume.domain;

import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.converter.TFCodeConverter;
import com.hckst.respal.members.domain.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    // 이력서 id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="RESUME_ID")
    private Long id;

    // 이력서 제목
    @Column(length = 20)
    private String title;
    // 이력서 내용
    @Column(length = 1000)
    private String content;
    // 파일 경로
    @Column(length = 255)
    private String filePath;
    // 게시물 조회수
    @Column(length = 10)
    private int views;

    // 대표 여부
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode mainYn;
    // 수정 여부
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode modifyYn;
    // 삭제 여부
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode deleteYn;

    // 등록일시
    private LocalDateTime regTime;
    // 수정일시
    private LocalDateTime modifyTime;
    // 삭제일시
    private LocalDateTime deleteTime;

    /**
     * 연관관계 매핑
     * 단방향
     * Members
     * Many to One
     */
    @ManyToOne
    @JoinColumn(name = "MEMBERS_ID")
    private Members members;


    /**
     * 연관관계 매핑
     * 양방향
     * Comment
     * One to Many
     */
    @OneToMany(mappedBy = "resume")
    private List<Comment> commentList;


    @Builder
    public Resume(String title, String content, String filePath, Members members){
        this.title = title;
        this.content = content;
        this.filePath = filePath;
        this.mainYn = TFCode.FALSE;
        this.modifyYn = TFCode.FALSE;
        this.deleteYn = TFCode.FALSE;
        this.regTime = LocalDateTime.now();
        this.members = members;
        this.commentList = new ArrayList<>();
    }

    public void updateResume(String title, String content, String filePath){
        this.title = title;
        this.content = content;
        this.filePath = filePath;
        this.modifyYn = TFCode.TRUE;
        this.modifyTime = LocalDateTime.now();
    }
    public void deleteResume(){
        this.deleteYn = TFCode.TRUE;
        this.deleteTime = LocalDateTime.now();
    }

}

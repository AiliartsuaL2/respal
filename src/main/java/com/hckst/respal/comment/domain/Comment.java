package com.hckst.respal.comment.domain;

import com.hckst.respal.converter.TFCode;
import com.hckst.respal.converter.TFCodeConverter;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Column(name="COMMENT_ID")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 댓글 내용
    @Column(length = 300)
    private String content;
    // x축 좌표
    private int xLocation;
    // y축 좌표
    private int yLocation;

    // 삭제 여부
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode deleteYn;

    private LocalDateTime regTime;
    private LocalDateTime deleteTime;

    /**
     * 연관관계
     * Resume
     * Many To One (양방향)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="RESUME_ID")
    private Resume resume;


    /**
     * 연관관계
     * Members
     * Many To One (양방향)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MEMBERS_ID")
    private Members members;

    @Builder
    public Comment(String content, int xLocation, int yLocation, Resume resume, Members members) {
        this.content = content;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.resume = resume;
        // 연관관계 정의
        this.resume.getCommentList().add(this);
        this.members = members;
        this.members.getCommentList().add(this);
        this.deleteYn = TFCode.FALSE;
        this.regTime = LocalDateTime.now();
    }

    public void delete(){
        this.deleteYn = TFCode.TRUE;
        this.deleteTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Comment)){
            return false;
        }
        Comment comment = (Comment) o;
        return Objects.equals(id,comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

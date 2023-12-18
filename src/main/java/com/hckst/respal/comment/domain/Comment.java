package com.hckst.respal.comment.domain;

import com.hckst.respal.converter.TFCode;
import com.hckst.respal.converter.TFCodeConverter;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import lombok.*;
import org.springframework.data.annotation.Id;
import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.Transient;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
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
    private String deleteYn;

    private LocalDateTime regTime;
    private LocalDateTime deleteTime;



    @Column(name = "resume_id")
    private Long resumeId;

    @Column(name = "members_id")
    private Long membersId;

    @Transient
    private Resume resume;

    @Transient
    private Members members;

    @Builder
    public Comment(String content, int xLocation, int yLocation, Resume resume, Members members) {
        this.content = content;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.resume = resume;
        this.members = members;
        this.deleteYn = TFCode.FALSE.getValue();
        this.regTime = LocalDateTime.now();
        this.resumeId = resume.getId();
        this.membersId = members.getId();
    }

    public void delete(){
        this.deleteYn = TFCode.TRUE.getValue();
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

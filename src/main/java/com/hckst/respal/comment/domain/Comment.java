package com.hckst.respal.comment.domain;

import com.hckst.respal.comment.presentation.dto.request.CreateCommentRequestDto;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import java.time.ZonedDateTime;
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

    public Comment(CreateCommentRequestDto dto, Resume resume, Members members) {
        validationForCreate(dto, resume, members);
        this.content = dto.getContent();
        this.xLocation = dto.getLocationX();
        this.yLocation = dto.getLocationY();
        this.resume = resume;
        this.members = members;
        this.deleteYn = TFCode.FALSE.getValue();
        this.regTime = LocalDateTime.now();
        this.resumeId = resume.getId();
        this.membersId = members.getId();
    }

    private void validationForCreate(CreateCommentRequestDto dto, Resume resume, Members members) {
        if(dto == null || dto.getContent() == null) {
            throw new ApplicationException(ErrorMessage.ILLEGAL_COMMENT_ARGUMENT_EXCEPTION);
        }
        if(resume == null) {
            throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_EXCEPTION);
        }
        if(members == null) {
            throw new ApplicationException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION);
        }
    }

    public static Comment convertByQuery(Long id, String content, int xLocation, int yLocation, Resume resume, Members members, String deleteYn, ZonedDateTime regTime) {
        Comment comment = new Comment();
        comment.id = id;
        comment.content = content;
        comment.xLocation = xLocation;
        comment.yLocation = yLocation;
        comment.resume = resume;
        comment.members = members;
        comment.deleteYn = deleteYn;
        comment.regTime = regTime.toLocalDateTime();
        comment.resumeId = resume.getId();
        comment.membersId = members.getId();
        return comment;
    }

    public void delete(Members members){
        validateForDelete(members);
        this.deleteYn = TFCode.TRUE.getValue();
        this.deleteTime = LocalDateTime.now();
    }

    private void validateForDelete(Members members) {
        if(TFCode.TRUE.getValue().equals(this.deleteYn)) {
            throw new ApplicationException(ErrorMessage.NOT_EXIST_COMMENT_EXCEPTION);
        }
        // 삭제하려는 주체가 해당 댓글의 주인이 아니거나, 이력서의 주인이 아닌경우 Exception
        if(members.getId().longValue() != this.membersId.longValue()
                || members.getId().longValue() != this.resume.getMembers().getId().longValue()) {
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_DELETE_EXCEPTION);
        }
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

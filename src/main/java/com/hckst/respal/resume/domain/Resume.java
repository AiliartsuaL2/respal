package com.hckst.respal.resume.domain;

import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.converter.ResumeTypeConverter;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.converter.TFCodeConverter;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.tag.domain.Tag;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Resume {
    // 이력서 id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 이력서 제목
    @Column(length = 20)
    private String title;
    // 게시물 조회수
    @Column(length = 10)
    private int views;
    // 수정 여부
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode modifyYn;
    // 삭제 여부
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode deleteYn;
    @Convert(converter = ResumeTypeConverter.class)
    @Column(columnDefinition = "varchar(10)", nullable=false)
    private ResumeType resumeType;
    // 등록일시
    private LocalDateTime regTime;
    // 수정일시
    private LocalDateTime modifyTime;
    // 삭제일시
    private LocalDateTime deleteTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBERS_ID")
    private Members members;
    @OneToOne
    private ResumeFile resumeFile;
    // R2DBC 관련 연관관계 매핑 x
    @Transient
    private List<Comment> commentList;
    @OneToMany(mappedBy = "resume")
    private List<Tag> tagList;

    public static Resume create(CreateResumeRequestDto requestDto) {
        Resume resume = new Resume();
        resume.title = requestDto.getTitle();
        resume.resumeType = ResumeType.findByType(requestDto.getResumeType());
        resume.modifyYn = TFCode.FALSE;
        resume.deleteYn = TFCode.FALSE;
        resume.regTime = LocalDateTime.now();
        resume.members = requestDto.getWriter();
        resume.commentList = new ArrayList<>();
        resume.resumeFile = requestDto.getResumeFile();
        resume.tagList = new ArrayList<>();
        return resume;
    }

    public static Resume convertByQuery(Long resumeId, Long resumeMembersId, String deleteYn) {
        Resume resume = new Resume();
        resume.id = resumeId;
        resume.members = Members.createProxy(resumeMembersId);
        resume.deleteYn = TFCode.findByValue(deleteYn);
        return resume;
    }

    public void updateResume(String title, ResumeFile resumeFile){
        this.title = Objects.requireNonNullElse(title, this.title);
        this.resumeFile = Objects.requireNonNullElse(resumeFile, this.resumeFile);
        this.modifyYn = TFCode.TRUE;
        this.modifyTime = LocalDateTime.now();
    }

    public void deleteResume(Members members){
        validateForDelete(members);
        this.deleteYn = TFCode.TRUE;
        this.deleteTime = LocalDateTime.now();
    }

    private void validateForDelete(Members members) {
        if(TFCode.TRUE.equals(this.deleteYn)) {
            throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_EXCEPTION);
        }
        if(members == null || !members.equals(this.getMembers())){
            throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_DELETE_EXCEPTION);
        }
    }

    public void view(Members members) {
        viewsCountUp(members);
        if(!hasHandlePermission(members)) {
            throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_VIEW_EXCEPTION);
        }
    }

    public boolean hasHandlePermission(Members members) {
        if(ResumeType.PUBLIC.equals(this.resumeType)) {
            return true;
        }
        if(this.members.equals(members)) {
            return true;
        }
        for (Tag tag : this.tagList) {
            if(tag.getMembers().equals(members)) {
                return true;
            }
        }
        return false;
    }

    // 조회수 증가
    private void viewsCountUp(Members members){
        if(this.members.equals(members)) {
            return;
        }
        this.views++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Resume)){
            return false;
        }
        Resume resume = (Resume) o;
        return Objects.equals(id,resume.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void deleteTag(Tag tag) {
        this.tagList.remove(tag);
    }

    public void validateForTag(Members writer) {
        // 공개 이력서인데 태그를 시도하는경우
        if(ResumeType.PUBLIC.equals(this.getResumeType())){
            throw new ApplicationException(ErrorMessage.CAN_NOT_TAG_PUBLIC_RESUME_EXCEPTION);
        }
        // 태그하려는이가 게시물의 주인이 아닌경우
        if(!this.getMembers().equals(writer)){
            throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_TAG_EXCEPTION);
        }
    }
}

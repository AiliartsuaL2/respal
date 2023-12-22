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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    // 이력서 id
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이력서 제목
    @Column(length = 20)
    private String title;
    // 이력서 내용
    @Column(length = 1000)
    private String content;
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

    @Convert(converter = ResumeTypeConverter.class)
    @Column(columnDefinition = "varchar(10)", nullable=false)
    private ResumeType resumeType;

    // 등록일시
    private LocalDateTime regTime;
    // 수정일시
    private LocalDateTime modifyTime;
    // 삭제일시
    private LocalDateTime deleteTime;

    /**
     * 연관관계 매핑
     * 양방향
     * Members
     * Many to One
     * 이력서의 주인
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBERS_ID")
    private Members members;

    /**
     * 연관관계 매핑
     * 단방향
     * ResumeFile
     * One to One
     * 이력서의 파일
     */
    @OneToOne
    private ResumeFile resumeFile;

    // R2DBC 관련 연관관계 매핑 x
    @Transient
    private List<Comment> commentList;

    /**
     * 연관관계 매핑
     * 양방향
     * Tag
     * One to Many (다대다 중간테이블)
     * 이력서에 언급한 언급 리스트
     */
    @OneToMany(mappedBy = "resume")
    private List<Tag> tagList;


    @Builder
    private Resume(String title, String content ,ResumeFile resumeFile , Members members, ResumeType resumeType){
        this.title = title;
        this.content = content;
        this.resumeType = resumeType;
        this.mainYn = TFCode.FALSE;
        this.modifyYn = TFCode.FALSE;
        this.deleteYn = TFCode.FALSE;
        this.regTime = LocalDateTime.now();
        this.members = members;
        this.commentList = new ArrayList<>();
        this.resumeFile = resumeFile;
        this.tagList = new ArrayList<>();
    }

    public static Resume create(CreateResumeRequestDto requestDto, ResumeFile resumeFile, Members writer, ResumeType resumeType) {
        return Resume.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getTitle())
                .resumeFile(resumeFile)
                .members(writer)
                .resumeType(resumeType)
                .build();
    }

    public static Resume convertByQuery(Long resumeId, Long resumeMembersId, String deleteYn) {
        Resume resume = new Resume();
        resume.id = resumeId;
        resume.members = Members.createProxy(resumeMembersId);
        resume.deleteYn = TFCode.findByValue(deleteYn);
        return resume;
    }

    public void updateResume(String title, String content, ResumeFile resumeFile){
        this.title = Objects.requireNonNullElse(title, this.title);
        this.content = Objects.requireNonNullElse(content, this.content);
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
        if(ResumeType.PUBLIC.equals(this.resumeType)) {
            return;
        }
        if(!isTagged(members)) {
            throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_VIEW_EXCEPTION);
        }
    }

    private boolean isTagged(Members members) {
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
}

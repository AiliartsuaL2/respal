package com.hckst.respal.tag.domain;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {
    // 멘션 ID
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 언급된 이력서
    @ManyToOne
    @JoinColumn(name = "RESUME_ID")
    private Resume resume;
    // 언급된 회원
    @ManyToOne
    @JoinColumn(name = "MEMBERS_ID")
    private Members members;
    // 언급시간
    private LocalDateTime regTime;

    public Tag(Resume resume, Members members){
        validateForCreate(resume, members);
        this.resume = resume;
        this.members = members;
        this.regTime = LocalDateTime.now();
        this.resume.getTagList().add(this);
        this.members.getTaggedList().add(this);
    }

    private void validateForCreate(Resume resume, Members members) {
        if(resume.getMembers().equals(members)) {
            throw new ApplicationException(ErrorMessage.CAN_NOT_TAG_ONESELF_EXCEPTION);
        }
    }

    public void remove(Members deleteMembers) {
        validateForDelete(deleteMembers);
        this.resume.deleteTag(this);
        this.members.deleteTag(this);
    }

    private void validateForDelete(Members deleteMembers) {
        // 삭제의 주체가 멘션당한 사람인 경우
        if(deleteMembers.equals(this.getMembers())) {
            return;
        }
        // 삭제의 주체가 이력서 주인인 경우
        if(deleteMembers.equals(this.getResume().getMembers())) {
            return;
        }
        throw new ApplicationException(ErrorMessage.PERMISSION_DENIED_TO_DELETE_EXCEPTION);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Tag)){
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package com.hckst.respal.tag.domain;

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
@AllArgsConstructor
@NoArgsConstructor
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
        this.resume = resume;
        this.members = members;
        this.regTime = LocalDateTime.now();
        this.resume.getTagList().add(this);
        this.members.getTaggedList().add(this);
    }

    public void remove() {
        this.resume.deleteTag(this);
        this.members.deleteTag(this);
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

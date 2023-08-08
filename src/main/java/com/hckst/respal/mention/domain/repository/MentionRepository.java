package com.hckst.respal.mention.domain.repository;

import com.hckst.respal.mention.domain.Mention;
import com.hckst.respal.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentionRepository extends JpaRepository<Mention,Long> , MentionRepositoryCustom {
    public List<Mention> findMentionByResume(Resume resume);
}

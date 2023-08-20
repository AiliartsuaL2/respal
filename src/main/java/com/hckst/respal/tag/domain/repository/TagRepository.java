package com.hckst.respal.tag.domain.repository;

import com.hckst.respal.tag.domain.Tag;
import com.hckst.respal.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> , TagRepositoryCustom {
    public List<Tag> findTagByResume(Resume resume);
}

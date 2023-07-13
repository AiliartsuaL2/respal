package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}

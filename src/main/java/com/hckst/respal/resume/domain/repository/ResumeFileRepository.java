package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.domain.ResumeFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeFileRepository extends JpaRepository<ResumeFile,Long> {
}

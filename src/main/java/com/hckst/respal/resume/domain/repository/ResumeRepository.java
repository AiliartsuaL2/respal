package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> , ResumeRepositoryCustom {
}

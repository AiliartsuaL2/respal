package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.converter.TFCode;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> , ResumeRepositoryCustom {
    final TFCode deleteStatus = TFCode.FALSE;
    Optional<Resume> findResumeByIdAndDeleteYn(long id,TFCode deleteYn);
}

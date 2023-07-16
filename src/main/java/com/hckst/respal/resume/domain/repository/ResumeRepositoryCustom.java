package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;

import java.util.List;
import java.util.Optional;

public interface ResumeRepositoryCustom {
    Optional<ResumeDetailResponseDto> findResumeDetailByResumeId(Long resumeId);
    List<ResumeDetailResponseDto> findResumeListByConditions(ResumeListRequestDto requestDto);
}

package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;

import java.util.List;
import java.util.Optional;

public interface ResumeRepositoryCustom {
    ResumeListResponseDto findResumeListByConditions(ResumeListRequestDto requestDto);
    Optional<Resume> findAllResumeById(long id);
}

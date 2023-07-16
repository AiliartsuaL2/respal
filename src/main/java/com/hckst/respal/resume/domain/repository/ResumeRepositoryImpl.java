package com.hckst.respal.resume.domain.repository;

import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;

import java.util.List;
import java.util.Optional;

public class ResumeRepositoryImpl implements ResumeRepositoryCustom{


    @Override
    public Optional<ResumeDetailResponseDto> findResumeDetailByResumeId(Long resumeId) {
        return null;
    }

    @Override
    public List<ResumeDetailResponseDto> findResumeListByConditions(ResumeListRequestDto requestDto) {
        return null;
    }
}

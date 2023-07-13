package com.hckst.respal.resume.application;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResumeService {
    private final ResumeRepository resumeRepository;
    public void createResume(CreateResumeRequestDto createResumeRequestDto){
        Resume resume = Resume.builder()
                .title(createResumeRequestDto.getTitle())
                .content(createResumeRequestDto.getContent())
                .filePath(createResumeRequestDto.getFilePath())
                .members(createResumeRequestDto.getMembers())
                .build();
        resumeRepository.save(resume);
    }
    public ResumeDetailResponseDto getResumeDetail(Long resumeId){
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID));
        return new ResumeDetailResponseDto(resume);
    }
}

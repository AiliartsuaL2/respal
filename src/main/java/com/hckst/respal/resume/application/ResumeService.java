package com.hckst.respal.resume.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.hckst.respal.comment.domain.repository.CommentRepository;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.ResumeFile;
import com.hckst.respal.resume.domain.repository.ResumeFileRepository;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import com.hckst.respal.resume.presentation.dto.response.CreateResumeFileResponseDto;
import com.hckst.respal.tag.application.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResumeService {
    private static final String BUCKET_NAME = "respal-resume";
    private final ResumeRepository resumeRepository;
    private final ResumeFileRepository resumeFileRepository;
    private final AmazonS3Client amazonS3Client;
    private final TagService tagService;

    /**
     * 이력서 추가 메서드
     * 회원 존재하지 않을시 401 떨어지므로 예외처리하지 않음
     */
    @Transactional
    public ResumeDetailResponseDto createResume(CreateResumeRequestDto createResumeRequestDto, Members members){
        ResumeFile resumeFile = resumeFileRepository.findById(createResumeRequestDto.getResumeFileId()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_FILE_ID_EXCEPTION));
        createResumeRequestDto.setResumeFileAndWriter(resumeFile, members);

        Resume resume = Resume.create(createResumeRequestDto);
        resumeRepository.save(resume);

        if(!(createResumeRequestDto.getTagIdList() == null)) {
            tagService.addTags(members, resume.getId(), createResumeRequestDto.getTagIdList());
        }

        ResumeDetailResponseDto resumeDetailResponseDto = new ResumeDetailResponseDto(resume);
        return resumeDetailResponseDto;
    }

    /**
     * 이력서 상세 조회 메서드
     * 이력서 유무 , delete_yn 컬럼값 확인하여 예외처리
     */
    @Transactional
    public ResumeDetailResponseDto getResumeDetailByResumeId(Long resumeId, Members members){
        // resume entity 가져오기
        Resume resume = resumeRepository.findAllResumeById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_EXCEPTION));

        resume.view(members);

        // DTO 변환
        ResumeDetailResponseDto resumeDetailResponseDto = new ResumeDetailResponseDto(resume);
        return resumeDetailResponseDto;
    }

    /**
     * 이력서 조회 메서드
     * Todo 조회하는 이력서 카테고리가 Hub인지 Mentioned인지 분기처리 로직 필요
     */
    public ResumeListResponseDto getResumeList(ResumeListRequestDto requestDto) {
        ResumeListResponseDto resumeList = resumeRepository.findResumeListByConditions(requestDto);
        return resumeList;
    }

    @Transactional
    /**
     * 이력서 파일 생성 메서드
     */
    public CreateResumeFileResponseDto createResumeFile(MultipartFile multipartFile) {
        String originalName = multipartFile.getOriginalFilename();
        ResumeFile resumeFile = ResumeFile.create(originalName);
        String filename = resumeFile.getStoredName();

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

            amazonS3Client.putObject(BUCKET_NAME, filename, multipartFile.getInputStream(), objectMetadata);

            String accessUrl = amazonS3Client.getUrl(BUCKET_NAME, filename).toString();
            resumeFile.setAccessUrl(accessUrl);
        } catch(IOException e) {
            throw new ApplicationException(ErrorMessage.FAILED_FILE_UPLOAD_TO_S3_EXCEPTION);
        }
        resumeFileRepository.save(resumeFile);
        return CreateResumeFileResponseDto.builder()
                .resumeFileId(resumeFile.getId())
                .originalName(resumeFile.getOriginName())
                .accessUrl(resumeFile.getAccessUrl())
                .build();
    }

    @Transactional
    public void removeResumeFile(Long resumeFileId) {
        ResumeFile resumeFile = resumeFileRepository.findById(resumeFileId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_FILE_ID_EXCEPTION));
        try {
            // 파일이 s3 서버에 있는지 확인
            boolean isObjectExist = amazonS3Client.doesObjectExist(BUCKET_NAME, resumeFile.getStoredName());
            if (!isObjectExist) {
                throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_FILE_ID_EXCEPTION);
            }

            amazonS3Client.deleteObject(BUCKET_NAME, resumeFile.getStoredName());
            // 삭제처리 (soft delete)
            resumeFile.deleteResumeFile();
        } catch (Exception e) {
            throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_FILE_ID_EXCEPTION);
        }
    }

    @Transactional
    public void removeResume(long resumeId, Members members) {
        Resume resume = resumeRepository.findAllResumeById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_EXCEPTION));

        resume.deleteResume(members);

        removeResumeFile(resume.getResumeFile().getId());
    }
}

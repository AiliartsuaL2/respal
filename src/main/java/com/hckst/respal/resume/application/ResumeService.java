package com.hckst.respal.resume.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.hckst.respal.comment.domain.repository.CommentRepository;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.converter.ResumeTypeConverter;
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
import com.hckst.respal.tag.presentation.dto.request.AddTagRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResumeService {
    private static String bucketName = "respal-resume";

    private final ResumeRepository resumeRepository;
    private final CommentRepository commentRepository;
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

        ResumeTypeConverter resumeTypeConverter = new ResumeTypeConverter();
        ResumeType resumeType = resumeTypeConverter.convertToEntityAttribute(createResumeRequestDto.getResumeType());
        if(resumeType == null){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_TYPE);
        }

        Resume resume = Resume.builder()
                .title(createResumeRequestDto.getTitle())
                .content(createResumeRequestDto.getContent())
                .resumeFile(resumeFile)
                .members(members)
                .resumeType(resumeType)
                .build();

        resumeRepository.save(resume);

        // 태그 추가
        AddTagRequestDto tagRequestDto = AddTagRequestDto.builder()
                .members(members)
                .resumeId(resume.getId())
                .membersIdList(createResumeRequestDto.getTagIdList())
                .build();
        tagService.addTag(tagRequestDto);


        ResumeDetailResponseDto resumeDetailResponseDto = ResumeDetailResponseDto.builder()
                .resume(resume)
                .commentList(new ArrayList<>())
                .build();
        return resumeDetailResponseDto;
    }

    /**
     * 이력서 상세 조회 메서드
     * 이력서 유무 , delete_yn 컬럼값 확인하여 예외처리
     */
    @Transactional
    public ResumeDetailResponseDto getResumeDetailByResumeId(Long resumeId){
        // resume entity 가져오기
        Resume resume = resumeRepository.findResumeJoinWithMembersById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID_EXCEPTION));
        /**
         * Resume entity 가져오기
         * 조회수 증가, 댓글 가져와서 Comment DTO로 변환
         * Resume DTO로 변환하여 반환
         */
        // 댓글
        List<CommentsResponseDto> comments = commentRepository.findCommentsDtoByResume(resume).orElse(new ArrayList<>());
        // 조회수 증가
        resume.viewsCountUp();

        // DTO 변환
        ResumeDetailResponseDto resumeDetailResponseDto = ResumeDetailResponseDto.builder()
                .resume(resume)
                .commentList(comments)
                .build();
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
    public CreateResumeFileResponseDto createResumeFile(MultipartFile multipartFile) {
        String originalName = multipartFile.getOriginalFilename();
        ResumeFile resumeFile = ResumeFile.builder().originName(originalName).build();
        String filename = resumeFile.getStoredName();

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

            amazonS3Client.putObject(bucketName, filename, multipartFile.getInputStream(), objectMetadata);

            String accessUrl = amazonS3Client.getUrl(bucketName, filename).toString();
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
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, resumeFile.getStoredName());
            if (isObjectExist) {
                amazonS3Client.deleteObject(bucketName, resumeFile.getStoredName());
            }else{
                throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_FILE_ID_EXCEPTION);
            }
            // 삭제처리 (soft delete)
            resumeFile.deleteResumeFile();
        } catch (Exception e) {
            throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_FILE_ID_EXCEPTION);
        }
    }

    @Transactional
    public void removeResume(long resumeId, Members members) {
        if(members == null){
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_DELETE_EXCEPTION);
        }
        Resume resume = resumeRepository.findResumeJoinWithMembersById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID_EXCEPTION));
        if(!members.equals(resume.getMembers())){
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_DELETE_EXCEPTION);
        }

        // 삭제처리 (soft delete)
        resume.deleteResume();

        // 파일도 삭제, 이력서는 반드시 파일을 가지고 있어야 하기 때문에
        removeResumeFile(resume.getResumeFile().getId());
    }
}

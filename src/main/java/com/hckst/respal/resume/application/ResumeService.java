package com.hckst.respal.resume.application;

import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.comment.domain.repository.CommentRepository;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final MembersRepository membersRepository;
    private final CommentRepository commentRepository;

    /**
     * 이력서 추가 메서드
     * 회원 존재하지 않을시 401 떨어지므로 예외처리하지 않음
     */
    @Transactional
    public void createResume(CreateResumeRequestDto createResumeRequestDto, long membersId){
        Members members = membersRepository.findMembersAndResumeById(membersId).get();
        Resume resume = Resume.builder()
                .title(createResumeRequestDto.getTitle())
                .content(createResumeRequestDto.getContent())
                .filePath(createResumeRequestDto.getFilePath())
                .members(members)
                .build();
        resumeRepository.save(resume);
    }

    /**
     * 이력서 상세 조회 메서드
     * 이력서 유무 , delete_yn 컬럼값 확인하여 예외처리
     */
    @Transactional
    public ResumeDetailResponseDto getResumeDetailByResumeId(Long resumeId){

        // resume entity 가져오기
        Resume resume = resumeRepository.findResumeByIdAndDeleteYn(resumeId,TFCode.FALSE).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID));
        /**
         * Resume entity 가져오기
         * 조회수 증가, 댓글 가져와서 DTO 변환
         * DTO 변환하여 반환
         */

        // 댓글
//        List<Comment> comments = commentRepository.findCommentByResumeAndDeleteYn(resume,TFCode.FALSE).orElse(new ArrayList<>());

        // 조회수 증가
        resume.viewsCountUp();

        // DTO 변환
        ResumeDetailResponseDto resumeDetailResponseDto = ResumeDetailResponseDto.builder()
                .resume(resume)
//                .commentList(comments)
                .build();
        return resumeDetailResponseDto;
    }

    public List<ResumeDetailResponseDto> getResumeList(ResumeListRequestDto requestDto) {
        return null;
    }
}

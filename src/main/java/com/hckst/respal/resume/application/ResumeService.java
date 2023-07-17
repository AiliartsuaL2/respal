package com.hckst.respal.resume.application;

import com.hckst.respal.comment.domain.Comment;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final MembersRepository membersRepository;

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
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID));
        if(TFCode.TRUE.equals(resume.getDeleteYn())){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID);
        }
        // 댓글
        List<CommentsResponseDto> commentList = resume.getCommentList().stream()
                // 삭제되지 않은 댓글들만 가져옴
                .filter(c -> TFCode.FALSE.equals(c.getDeleteYn()))
                .map(c -> CommentsResponseDto.builder()
                        .id(c.getId())
                        .content(c.getContent())
                        .xLocation(c.getXLocation())
                        .yLocation(c.getYLocation())
                        .membersId(c.getMembers().getId())
                        .membersPicture(c.getMembers().getPicture())
                        .membersNickname(c.getMembers().getNickname())
                        .build())
                .collect(Collectors.toList());
        ResumeDetailResponseDto resumeDetailResponseDto = new ResumeDetailResponseDto(resume,commentList);
        // 조회수 증가
        resume.viewsCountUp();

        return resumeDetailResponseDto;
    }

    public List<ResumeDetailResponseDto> getResumeList(ResumeListRequestDto requestDto) {
        return null;
    }
}

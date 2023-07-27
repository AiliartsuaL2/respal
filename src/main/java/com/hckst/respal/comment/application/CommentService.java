package com.hckst.respal.comment.application;

import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.comment.domain.repository.CommentRepository;
import com.hckst.respal.comment.presentation.dto.request.CreateCommentRequestDto;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.resume.domain.Resume;
import com.hckst.respal.resume.domain.repository.ResumeRepository;
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
public class CommentService {
    private final CommentRepository commentRepository;
    private final ResumeRepository resumeRepository;
    private final MembersRepository membersRepository;

    /**
     * 댓글 생성 메서드
     * members : 댓글 작성자 / 없으면 401 떨어지므로 예외처리 하지않음
     * resume : 이력서 / 존재하지 않음과 delete_yn 컬럼 상태를 확인하여 예외처리
     */
    @Transactional
    public void createComment(CreateCommentRequestDto requestDto, long membersId, long resumeId){
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID_EXCEPTION));
        // delete_yn 컬럼이 Y인경우
        if(TFCode.TRUE.equals(resume.getDeleteYn())){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_ID_EXCEPTION);
        }
        Members members = membersRepository.findMembersAndCommentById(membersId).get();

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .xLocation(requestDto.getXLocation())
                .yLocation(requestDto.getYLocation())
                .members(members)
                .resume(resume)
                .build();
        commentRepository.save(comment);
    }

    /**
     * 댓글 삭제 메서드
     * 댓글이 없거나 delteYn컬럼이 Y인경우 예외처리
     * 회원이 댓글의 작성자이거나 이력서 작성자여야함
     */
    @Transactional
    public void deleteComment(Long commentId, Members members){
        // 댓글이 존재하지 않는경우
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_COMMENT_EXCEPTION));
        if(TFCode.TRUE.equals(comment.getDeleteYn())){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_COMMENT_EXCEPTION);
        }
        // 삭제 권한이 없는경우
        if(!(members.equals(comment.getMembers()) || members.equals(comment.getResume().getMembers()))){
            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_DELETE_EXCEPTION);
        }
        comment.delete();
    }
}

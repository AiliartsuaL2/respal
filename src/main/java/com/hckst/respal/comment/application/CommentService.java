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
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final ResumeRepository resumeRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final Sinks.Many<CommentsResponseDto> commentUpdateSink = Sinks.many().multicast().onBackpressureBuffer();

    public Mono<CommentsResponseDto> createComment(CreateCommentRequestDto requestDto, Members members, long resumeId){
        Resume resume = resumeRepository.findByIdAndDeleteYn(resumeId, TFCode.FALSE).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_EXCEPTION));
        Comment comment = new Comment(requestDto, resume, members);

        return commentRepository.save(comment)
                .map(CommentsResponseDto::create)
                .doOnNext(commentUpdateSink::tryEmitNext);
    }

    public Flux<ServerSentEvent<CommentsResponseDto>> findByResumeId(Long resumeId) {
        Flux<ServerSentEvent<CommentsResponseDto>> existComment = commentRepository.findAllCommentsByResumeId(resumeId)
                .map(comment -> ServerSentEvent.builder(CommentsResponseDto.create(comment)).build());

        Flux<ServerSentEvent<CommentsResponseDto>> updatedComment = commentUpdateSink.asFlux()
                .map(comment -> ServerSentEvent.builder(comment).build());

        // 두 Flux를 merge하여 하나의 SSE 스트림으로 합침
        return Flux.merge(existComment, updatedComment)
                .doOnCancel(() -> commentUpdateSink.asFlux().blockLast());
    }

    /**
     * 댓글 삭제 메서드
     * 댓글이 없거나 delteYn컬럼이 Y인경우 예외처리
     * 회원이 댓글의 작성자이거나 이력서 작성자여야함
     */
//    @Transactional
//    public void deleteComment(Long commentId, Members members){
//        // 댓글이 존재하지 않는경우
//        Mono<Comment> comment = commentRepository.findById(commentId);
//        if(TFCode.TRUE.equals(comment.getDeleteYn())){
//            throw new ApplicationException(ErrorMessage.NOT_EXIST_COMMENT_EXCEPTION);
//        }
//        // 삭제 권한이 없는경우
//        if(!(members.equals(comment.getMembers()) || members.equals(comment.getResume().getMembers()))){
//            throw new ApplicationException(ErrorMessage.PERMITION_DENIED_TO_DELETE_EXCEPTION);
//        }
//        comment.delete();
//    }
}

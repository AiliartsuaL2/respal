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
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final ResumeRepository resumeRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final Map<Long, Many<CommentsResponseDto>> commentUpdateSinks = new HashMap<>();

    private Sinks.Many<CommentsResponseDto> getOrCreateSink(Long resumeId) {
        return commentUpdateSinks.computeIfAbsent(resumeId, key -> Sinks.many().multicast().onBackpressureBuffer());
    }

    public Mono<CommentsResponseDto> createComment(CreateCommentRequestDto requestDto, Members members, long resumeId){
        Many<CommentsResponseDto> commentUpdateSink = getOrCreateSink(resumeId);
        Resume resume = resumeRepository.findByIdAndDeleteYn(resumeId, TFCode.FALSE).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_RESUME_EXCEPTION));
        Comment comment = new Comment(requestDto, resume, members);

        return commentRepository.save(comment)
                .map(CommentsResponseDto::create)
                .doOnNext(commentUpdateSink::tryEmitNext);
    }

    public Flux<ServerSentEvent<CommentsResponseDto>> findByResumeId(Long resumeId) {
        Many<CommentsResponseDto> commentUpdateSink = getOrCreateSink(resumeId);
        Flux<ServerSentEvent<CommentsResponseDto>> existComment = commentRepository.findAllCommentsByResumeId(resumeId)
                .map(comment -> ServerSentEvent.builder(CommentsResponseDto.convert(comment)).build());

        Flux<ServerSentEvent<CommentsResponseDto>> updatedComment = commentUpdateSink.asFlux()
                .map(commentDto -> ServerSentEvent.builder(commentDto).build());

        // 두 Flux를 merge하여 하나의 SSE 스트림으로 합침
        return Flux.merge(existComment, updatedComment)
                .doOnCancel(() -> commentUpdateSink.asFlux().blockLast());
    }

    /**
     * 댓글 삭제 메서드
     * 댓글이 없거나 delteYn컬럼이 Y인경우 예외처리
     * 회원이 댓글의 작성자이거나 이력서 작성자여야함
     * Mono 반환
     */
    public Mono<CommentsResponseDto> deleteComment(Long commentId, Members members){
        return commentRepository.findCommentWithResumeById(commentId)
                .flatMap(comment -> {
                    comment.delete(members);
                    return commentRepository.save(comment);
                }).map(CommentsResponseDto::delete)
                .doOnNext(comment -> {
                    getOrCreateSink(comment.getResumeId())
                            .tryEmitNext(comment);
                });
    }
}

package com.hckst.respal.comment.presentation;

import com.hckst.respal.comment.application.CommentService;
import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.comment.presentation.dto.request.CreateCommentRequestDto;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.members.domain.Members;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{resumeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiCommonResponse<CommentsResponseDto>>> create(@RequestBody CreateCommentRequestDto createCommentRequestDto,
                                                                         @AuthenticationPrincipal Members members,
                                                                         @PathVariable Long resumeId) {
        return commentService.createComment(createCommentRequestDto, members, resumeId)
                        .map(dto -> ResponseEntity
                                .ok()
                                .body(ApiCommonResponse.<CommentsResponseDto>builder()
                                        .statusCode(201)
                                        .result(dto)
                                        .build()));
    }

    @GetMapping("/{resumeId}")
    public Flux<ServerSentEvent<CommentsResponseDto>> findCommentByResumeId(@PathVariable Long resumeId) {
        return commentService.findByResumeId(resumeId);
    }

    @DeleteMapping("/{resumeId}")
    public Mono<ResponseEntity<ApiCommonResponse<CommentsResponseDto>>> create(@AuthenticationPrincipal Members members, @PathVariable Long resumeId) {
        return commentService.deleteComment(resumeId, members)
                .map(dto -> ResponseEntity
                        .ok()
                        .body(ApiCommonResponse.<CommentsResponseDto>builder()
                                .statusCode(200)
                                .result(dto)
                                .build()));
    }
}

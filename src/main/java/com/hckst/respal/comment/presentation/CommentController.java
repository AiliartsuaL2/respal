package com.hckst.respal.comment.presentation;

import com.hckst.respal.comment.application.CommentService;
import com.hckst.respal.comment.presentation.dto.request.CreateCommentRequestDto;
import com.hckst.respal.comment.presentation.dto.response.CommentsResponseDto;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.members.domain.Members;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "댓글 생성 API", description = "댓글 생성 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성된 댓글의 data가 응답됩니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "resumeId에 해당하는 이력서가 존재하지 않는경우 응답됩니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 이력서에 대한 댓글 생성 권한이 존재하지 않는경우 응답됩니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{resumeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ApiCommonResponse<CommentsResponseDto>>> create(
            @Parameter(description = "댓글 생성에 필요한 데이터를 갖고있는 DTO입니다.")
            @RequestBody CreateCommentRequestDto createCommentRequestDto,
            @Parameter(description = "댓글을 생성하는 회원입니다. 인증 토큰을 통해 자동으로 매핑됩니다. 권한이 없는경우 403 코드가 응답됩니다.")
            @AuthenticationPrincipal Members members,
            @Parameter(description = "댓글을 생성할 이력서 ID 입니다.")
            @PathVariable Long resumeId) {
        return commentService.createComment(createCommentRequestDto, members, resumeId)
                        .map(dto -> ResponseEntity
                                .ok()
                                .body(new ApiCommonResponse<>(201, dto)));
    }

    @Operation(summary = "댓글 조회 API", description = "댓글 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글의 상세 정보가 응답됩니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "resumeId에 해당하는 이력서가 존재하지 않는경우 응답됩니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 이력서에 대한 댓글 조회 권한이 존재하지 않는경우 응답됩니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{resumeId}")
    public Flux<ServerSentEvent<CommentsResponseDto>> findCommentByResumeId(
            @Parameter(description = "조회 할 댓글의 이력서 ID 입니다.")
            @PathVariable Long resumeId,
            @Parameter(description = "댓글을 조회하는 회원입니다. 인증 토큰을 통해 자동으로 매핑됩니다. 권한이 없는경우 403 코드가 응답됩니다.")
            @AuthenticationPrincipal Members members) {
        return commentService.findByResumeId(resumeId, members);
    }

    @Operation(summary = "댓글 제거 API", description = "댓글 제거 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글이 성공적으로 삭제될 경우 응답됩니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "commentId에 해당하는 댓글이 존재하지 않는 경우 응답됩니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 댓글의 삭제에 대한 권한이 존재하지 않는 경우 응답됩니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{commentId}")
    public Mono<ResponseEntity<ApiCommonResponse<CommentsResponseDto>>> create(@AuthenticationPrincipal Members members, @PathVariable Long commentId) {
        return commentService.deleteComment(commentId, members)
                .map(dto -> ResponseEntity
                        .ok()
                        .body(new ApiCommonResponse<>(200, dto)));
    }
}

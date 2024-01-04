package com.hckst.respal.tag.presentation;

import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.tag.application.TagService;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TagController {
    private final TagService tagService;

    @Operation(summary = "태그 생성 API", description = "태그를 생성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "태그 생성에 성공한 경우 응답됩니다. ", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 이력서, 공개 이력서의 경우 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "API 접근 권한이 없는 경우(토큰 미존재) 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "태그 생성 권한이 없는 경우 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/tag/{resumeId}")
    public ResponseEntity<ApiCommonResponse<?>> createTag(
            @Parameter(description = "태그를 당하는 회원의 ID List입니다.")
            @RequestBody List<Long> taggedIdList,
            @Parameter(description = "태그를 할 이력서 ID 입니다.")
            @PathVariable Long resumeId,
            @Parameter(description = "태그를 하는 회원(주체자)입니다. 인증 토큰을 통해 자동으로 매핑됩니다.")
            @AuthenticationPrincipal Members members){
        tagService.addTags(members, resumeId, taggedIdList);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "태그 제거 API", description = "태그를 제거하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "태그 제거에 성공한 경우 응답됩니다. ", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 이력서의 경우 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "API 접근 권한이 없는 경우(토큰 미존재) 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "태그 제거 권한이 없는 경우 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/tag/{tagId}")
    public ResponseEntity<ApiCommonResponse<?>> removeTag(
            @Parameter(description = "제거 당할 태그에 해당하는 회원 ID 입니다.")
            @PathVariable Long taggedMembersId,
            @Parameter(description = "태그를 제거하는 회원입니다. 인증 토큰을 통해 자동으로 매핑됩니다.")
            @AuthenticationPrincipal Members members){
        tagService.removeTag(taggedMembersId, members);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

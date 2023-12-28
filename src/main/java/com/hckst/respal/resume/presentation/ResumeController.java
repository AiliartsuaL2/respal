package com.hckst.respal.resume.presentation;

import com.hckst.respal.converter.ResumeType;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.application.ResumeService;
import com.hckst.respal.resume.domain.ResumeFile;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.CreateResumeFileResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    private final ResumeService resumeService;
    @Operation(summary = "이력서 상세 조회 API", description = "이력서 상세 조회 API입니다. 조회하려는 이력서의 seq값을 path로 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력서 상세", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "seq에 해당하는 이력서가 존재하지 않음.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 이력서의 열람 권한이 없음", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/resume/{resumeId}")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<ResumeDetailResponseDto>> getResumeDetail(@PathVariable long resumeId, @AuthenticationPrincipal Members members){
        ResumeDetailResponseDto resumeDetail = resumeService.getResumeDetailByResumeId(resumeId, members);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(resumeDetail)
                .build();
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "이력서 조회 API", description = "이력서 조회 API 입니다. Paramter를 통해 type, page, limit, 정렬 조건을 지정 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "HubList를 반환합니다.", useReturnTypeSchema = true)
    })
    @GetMapping("/resume")
    public ResponseEntity<ApiCommonResponse<ResumeListResponseDto>> getResumes(
            @RequestParam String type,
            @RequestParam int page,
            @RequestParam int limit,
            @AuthenticationPrincipal Members viewer){
        ResumeListRequestDto requestDto = ResumeListRequestDto.create(type, page, limit, viewer);
        ResumeListResponseDto resumeList = resumeService.getResumeList(requestDto);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(resumeList)
                .build();

        return ResponseEntity.ok(response);
    }
    @Operation(summary = "이력서 생성 API", description = "이력서를 생성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이력서 생성에 성공하였습니다. ", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "이력서 생성 권한이 없는경우 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/resume")
    public ResponseEntity<ApiCommonResponse<ResumeDetailResponseDto>> createResume(@Valid @RequestBody CreateResumeRequestDto requestDto, @AuthenticationPrincipal Members members){
        ResumeDetailResponseDto savedResume = resumeService.createResume(requestDto, members);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(201)
                .result(savedResume)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "이력서 삭제 API", description = "이력서를 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "이력서 삭제에 성공 할 경우 발생하는 응답입니다. ", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "이력서가 존재하지 않는 경우 발생하는 응답입니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "이력서 삭제 권한이 없는경우 발생하는 응답입니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/resume/{resumeId}")
    public ResponseEntity<ApiCommonResponse<?>> removeResume(@PathVariable long resumeId, @AuthenticationPrincipal Members members){
        resumeService.removeResume(resumeId, members);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(204)
                .result(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "이력서 파일 저장 API", description = "이력서 파일을 저장하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이력서의 파일 저장에 성공하는경우 응답입니다. ", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "S3 서버 업로드시 에러가 발생하는경우 발생하는 응답입니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/resume/file")
    public ResponseEntity<ApiCommonResponse<ResumeDetailResponseDto>> createResumeFile(@RequestPart("resumeFile") MultipartFile requestDto){
        CreateResumeFileResponseDto resumeFile = resumeService.createResumeFile(requestDto);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(201)
                .result(resumeFile)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "이력서 파일 삭제 API", description = "이력서 파일을 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "이력서의 파일 삭제에 성공하는경우 응답입니다. "),
            @ApiResponse(responseCode = "400", description = "이력서 파일이 존재하지 않는경우 발생하는 응답입니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "S3 파일 삭제 에러가 발생하는경우 발생하는 응답입니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/resume/file")
    public ResponseEntity<ApiCommonResponse<?>> removeResumeFile(@RequestParam Long resumeFileId){
        resumeService.removeResumeFile(resumeFileId);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(204)
                .result(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}

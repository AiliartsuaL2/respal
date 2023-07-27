package com.hckst.respal.resume.presentation;

import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.application.ResumeService;
import com.hckst.respal.resume.presentation.dto.request.CreateResumeRequestDto;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    private final ResumeService resumeService;
    @Operation(summary = "이력서 상세 조회 API", description = "이력서 상세 조회 API입니다. 조회하려는 이력서의 seq값을 path variable로 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력서 상세", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "seq에 해당하는 이력서가 존재하지 않음.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/resume/{seq}")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<ResumeDetailResponseDto>> getResumeDetail(@PathVariable long seq){
        ResumeDetailResponseDto resumeDetail = resumeService.getResumeDetailByResumeId(seq);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(resumeDetail)
                .build();
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "이력서 리스트 조회 API", description = "이력서 리스트를 조회하는 API입니다. 정렬조건, 검색 조건을 통해서 조회 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력서 ", useReturnTypeSchema = true)
    })
    @GetMapping("/resume")
    public ResponseEntity<ApiCommonResponse<List<ResumeDetailResponseDto>>> getResumeList(ResumeListRequestDto requestDto){
        ResumeListResponseDto resumeList = resumeService.getResumeList(requestDto);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(resumeList)
                .build();
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "이력서 생성 API", description = "이력서를 생성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이력서 ", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "이력서 생성 권한이 없는경우 발생합니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/resume")
    public ResponseEntity<ApiCommonResponse<ResumeDetailResponseDto>> createResume(CreateResumeRequestDto requestDto, @AuthenticationPrincipal Members members){
        ResumeDetailResponseDto savedResume = resumeService.createResume(requestDto, members.getId());
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(savedResume)
                .build();
        return ResponseEntity.ok(response);
    }
}

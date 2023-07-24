package com.hckst.respal.resume.presentation;

import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.resume.application.ResumeService;
import com.hckst.respal.resume.presentation.dto.request.ResumeListRequestDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeDetailResponseDto;
import com.hckst.respal.resume.presentation.dto.response.ResumeListResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ResumeController {
    private final ResumeService resumeService;
    public ResponseEntity<ApiCommonResponse<ResumeDetailResponseDto>> getResumeDetail(long resumeSeq){
        ResumeDetailResponseDto resumeDetail = resumeService.getResumeDetailByResumeId(resumeSeq);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(resumeDetail)
                .build();
        return ResponseEntity.ok(response);
    }
    public ResponseEntity<ApiCommonResponse<List<ResumeDetailResponseDto>>> getResumeList(ResumeListRequestDto requestDto){
        ResumeListResponseDto resumeList = resumeService.getResumeList(requestDto);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(resumeList)
                .build();
        return ResponseEntity.ok(response);

    }
}

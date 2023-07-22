package com.hckst.respal.resume.presentation.dto.request;

import com.hckst.respal.converter.Sort;
import com.hckst.respal.members.domain.Job;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "이력서 리스트 요청 DTO")
public class ResumeListRequestDto {
    // 페이지 넘버
    @NotNull
    private int page;
    // 요청당 페이지 수
    @NotNull
    private int limit;
    // 필터( 직업)
    private Job job;
    // 정렬 조건
    private Sort sort;
}

package com.hckst.respal.resume.presentation.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeListResponseDto {
    private List<ResumeDetailResponseDto> resumeList;
    private long resumeListCount;
}

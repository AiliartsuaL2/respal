package com.hckst.respal.resume.presentation.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateResumeFileResponseDto {
    private Long resumeFileId;
    private String accessUrl;
    private String originalName;
}

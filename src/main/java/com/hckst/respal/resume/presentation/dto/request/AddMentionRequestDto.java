package com.hckst.respal.resume.presentation.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMentionRequestDto {
    private Long resumeId;
    private Long membersId;
}

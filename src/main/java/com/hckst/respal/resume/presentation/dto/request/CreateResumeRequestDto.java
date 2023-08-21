package com.hckst.respal.resume.presentation.dto.request;


import com.hckst.respal.members.domain.Members;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "이력서 작성 요청 DTO")
public class CreateResumeRequestDto {
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private Long resumeFileId;
    @NotNull
    private String resumeType;
    // 태그 할 회원 Id List
    private List<Long> tagIdList;
}

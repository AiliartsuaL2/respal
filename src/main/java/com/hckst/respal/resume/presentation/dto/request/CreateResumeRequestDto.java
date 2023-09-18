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
    @NotNull(message = "제목은 필수 입력값이에요")
    private String title;
    @NotNull(message = "내용은 필수 입력값이에요")
    private String content;
    @NotNull(message = "파일 ID는 필수 입력값이에요")
    private Long resumeFileId;
    @NotNull(message = "이력서 타입은 필수 입력값이에요 (public , private)")
    private String resumeType;
    // 태그 할 회원 Id List
    private List<Long> tagIdList;
}

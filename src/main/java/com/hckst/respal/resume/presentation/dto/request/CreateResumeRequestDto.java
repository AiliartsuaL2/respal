package com.hckst.respal.resume.presentation.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.ResumeFile;
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
    @Schema(description = "이력서 제목 입니다.")
    @NotNull(message = "제목은 필수 입력값이에요")
    private String title;
    @Schema(description = "이력서 내용 입니다.")
    @NotNull(message = "내용은 필수 입력값이에요")
    private String content;
    @Schema(description = "등록된 이력서 파일의 ID입니다.")
    @NotNull(message = "파일 ID는 필수 입력값이에요")
    private Long resumeFileId;
    @Schema(description = "이력서의 타입입니다.", allowableValues={"public","private"})
    @NotNull(message = "이력서 타입은 필수 입력값이에요 (public , private)")
    private String resumeType;
    @Schema(description = "등록시 태그할 회원의 ID 리스트 입니다.")
    private List<Long> tagIdList;

    /**
     * 내부 값 전달용 필드
     */
    @Schema(hidden = true)
    @JsonIgnore
    private ResumeFile resumeFile;
    @Schema(hidden = true)
    @JsonIgnore
    private Members writer;

    public void setResumeFileAndWriter(ResumeFile resumeFile, Members writer) {
        this.resumeFile = resumeFile;
        this.writer = writer;
    }
}

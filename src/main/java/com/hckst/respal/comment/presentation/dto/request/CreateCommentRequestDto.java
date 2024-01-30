package com.hckst.respal.comment.presentation.dto.request;

import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.CommonRequestDto;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.resume.domain.Resume;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@Schema(description = "댓글 생성 요청 DTO")
public class CreateCommentRequestDto extends CommonRequestDto {
    @Schema(description = "내용", nullable = false)
    @NotNull(message = "내용은 필수 입력 항목이에요")
    private String content;
    @Schema(description = "댓글의 x좌표", nullable = false)
    @NotNull(message = "댓글의 x좌표는 필수 입력 항목이에요")
    private int locationX;
    @Schema(description = "내용", nullable = false)
    @NotNull(message = "댓글의 y좌표는 필수 입력 항목이에요")
    private int locationY;

    @Override
    public void checkRequiredFieldIsNull() {
        checkNull(this.content, ErrorMessage.NOT_EXIST_CONTENT_EXCEPTION);
        checkNull(this.locationX, ErrorMessage.NOT_EXIST_LOCATION_X_EXCEPTION);
        checkNull(this.locationY, ErrorMessage.NOT_EXIST_LOCATION_Y_EXCEPTION);
    }
}

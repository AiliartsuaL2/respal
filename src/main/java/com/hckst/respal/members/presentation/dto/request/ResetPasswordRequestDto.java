package com.hckst.respal.members.presentation.dto.request;

import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.CommonRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "비밀번호 재설정 요청 DTO")
public class ResetPasswordRequestDto extends CommonRequestDto {
    @Schema(description = "이메일", nullable = false)
    @NotNull(message = "이메일은 필수 입력 항목이에요")
    private String email;
    @Schema(description = "기존 비밀번호", nullable = false)
    @NotNull(message = "기존 비밀번호는 입력 항목이에요")
    private String existPassword;
    @Schema(description = "재설정 할 새로운 비밀번호", nullable = false)
    @NotNull(message = "재설정 할 새로운 비밀번호는 필수 입력 항목이에요")
    private String newPassword;

    @Override
    public void checkRequiredFieldIsNull() {
        checkNull(this.email, ErrorMessage.NOT_EXIST_MEMBER_EMAIL_EXCEPTION);
        checkNull(this.existPassword, ErrorMessage.NOT_EXIST_MEMBER_PASSWORD_EXCEPTION);
        checkNull(this.newPassword, ErrorMessage.NOT_EXIST_MEMBER_PASSWORD_EXCEPTION);
    }
}

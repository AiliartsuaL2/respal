package com.hckst.respal.members.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "비밀번호 재설정 요청")
public class PasswordPatchRequestDto {
    @Schema(description = "이메일", nullable = false)
    @NotNull(message = "이메일은 필수 입력 항목이에요")
    private String email;
    @Schema(description = "기존 비밀번호", nullable = false)
    @NotNull(message = "기존 비밀번호는 입력 항목이에요")
    private String tmpPassword;
    @Schema(description = "재설정 할 새로운 비밀번호", nullable = false)
    @NotNull(message = "재설정 할 새로운 비밀번호는 필수 입력 항목이에요")
    private String newPassword;
}

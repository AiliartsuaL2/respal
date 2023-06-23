package com.hckst.respal.members.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "비밀번호 재설정 요청")
public class PasswordPatchRequestDto {
    @Schema(description = "이메일", nullable = false)
    private String email;
    @Schema(description = "기존 비밀번호", nullable = false)
    private String tmpPassword;
    @Schema(description = "재설정한 새로운 비밀번호", nullable = false)
    private String newPassword;
}

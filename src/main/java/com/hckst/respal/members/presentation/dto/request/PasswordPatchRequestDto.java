package com.hckst.respal.members.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "비밀번호 재설정 요청")
public class PasswordPatchRequestDto {
    @Schema(description = "재설정 할 비밀번호", nullable = false)
    private String password;
    @Schema(description = "암호화된 email(redirect url의 query string 값)", nullable = false)
    private String uid;
}

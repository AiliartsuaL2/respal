package com.hckst.respal.members.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일반 이메일 로그인 요청")
public class MembersLoginRequestDto {
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    private String email;
    @Schema(description = "비밀번호", nullable = false)
    private String password;
}

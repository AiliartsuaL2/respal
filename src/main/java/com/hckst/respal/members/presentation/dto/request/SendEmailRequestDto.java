package com.hckst.respal.members.presentation.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "비밀번호 재설정 Direction 요청")
public class SendEmailRequestDto {
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    @NotNull(message = "이메일은 필수 입력 항목이에요")
    private String email;
    @Schema(description = "임시 비밀번호")
    private String tmpPassword;


}

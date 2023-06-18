package com.hckst.respal.members.presentation.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "비밀번호 재설정 Direction 요청")
public class SendEmailRequestDto {
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    private String email;
    private String uid;

}

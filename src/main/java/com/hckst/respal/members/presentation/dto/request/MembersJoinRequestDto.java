package com.hckst.respal.members.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일반 이메일 회원 가입 요청")
public class MembersJoinRequestDto {
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    private String email;
    @Schema(description = "비밀번호", nullable = false)
    private String password;
    @Schema(description = "닉네임", nullable = false)
    @Email
    private String nickname;
    @Schema(description = "사진", nullable = false)
    private String picture;
    @Schema(description = "provider", nullable = false, allowableValues = {"common","kakao","google","github"})
    private String provider;

}

package com.hckst.respal.members.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "일반 이메일 회원 가입 요청")
public class MembersJoinRequestDto {
    @Schema(description = "이메일", nullable = false, example = "abc@jiniworld.me")
    @Email
    @NotNull
    private String email;
    @Schema(description = "비밀번호")

//    @Pattern(regexp = "[0-9]{8,16}", message = "핸드폰 번호는 10~11자리의 숫자만 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\\\|\\[\\]{};:'\",<.>/?]).{8,16}$",
            message = "비밀번호는 영문 소문자, 대문자, 숫자, 특수문자를 각각 1개 이상 포함하고, 8자리 이상 16자리 이하여야 합니다.")
    private String password;
    @Schema(description = "닉네임", nullable = false)
    private String nickname;
    @Schema(description = "사진", nullable = false)
    private String picture;
    @Schema(description = "provider", nullable = false, allowableValues = {"common","kakao","google","github"})
    @NotNull
    private String provider;

}

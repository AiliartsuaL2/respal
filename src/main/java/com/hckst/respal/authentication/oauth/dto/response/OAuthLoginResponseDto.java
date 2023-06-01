package com.hckst.respal.authentication.oauth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
@Builder
@Schema(description = "OAuth 기존 회원 로그인시 응답")
public class OAuthLoginResponseDto {
    @Schema(description = "토큰 grant type", example = "bearer")
    private String grantType;
    @Schema(description = "액세스 토큰")
    private String accessToken;
    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;


    @Email
    @Schema(description = "이메일", example = "abc@jiniworld.me")
    private String membersEmail;
    @Schema(description = "닉네임", example = "ailiartsua")
    private String nickname;
    @Schema(description = "이미지", example = "ailiartsua")
    private String image;
}

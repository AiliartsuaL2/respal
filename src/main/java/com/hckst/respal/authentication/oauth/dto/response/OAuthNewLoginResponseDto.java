package com.hckst.respal.authentication.oauth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "OAuth 비회원 로그인시 응답")
public class OAuthNewLoginResponseDto {
    @Schema(description = "리다이렉트 Url", example = "/oauth/login/kakao")
    private String redirectUrl;

    //Todo 해당 accesstoken은 header에 넣기
    @Schema(description = "oauthAccessToken")
    private String oauthAccessToken;
}

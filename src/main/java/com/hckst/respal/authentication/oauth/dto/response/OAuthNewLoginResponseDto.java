package com.hckst.respal.authentication.oauth.dto.response;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "OAuth 비회원 로그인시 응답")
public class OAuthNewLoginResponseDto {
    @Schema(description = "provider", example = "kakao")
    private String provider;

    @Schema(description = "회원 정보")
    private UserInfo userInfo;
}

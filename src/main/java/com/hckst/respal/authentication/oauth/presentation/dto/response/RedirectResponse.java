package com.hckst.respal.authentication.oauth.presentation.dto.response;

import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@AllArgsConstructor
@Schema(description = "OAuth 로그인 Redirect시 응답")
public class RedirectResponse {
    @Schema(description = "유저 정보")
    private UserInfo userInfo;
    @Schema(description = "provider", nullable = false, allowableValues = {"common","kakao","google","github"})
    private String provider;
}

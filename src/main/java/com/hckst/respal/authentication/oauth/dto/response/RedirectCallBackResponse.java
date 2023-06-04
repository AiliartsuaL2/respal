package com.hckst.respal.authentication.oauth.dto.response;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Schema(description = "OAuth 로그인 Redirect시 신규회원 응답")
public class RedirectCallBackResponse extends RedirectResponse{
    @Schema(description = "액세스 토큰")
    private String accessToken;
    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;

    public RedirectCallBackResponse(UserInfo userInfo, String provider, String accessToken, String refreshToken) {
        super(userInfo,provider);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}

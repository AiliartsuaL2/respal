package com.hckst.respal.authentication.oauth.presentation.dto.response;

import com.hckst.respal.authentication.oauth.domain.RedirectType;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
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

    public RedirectResponse of(RedirectType redirectType) {
        // call back이면 토큰까지 반환
        if(RedirectType.CALL_BACK.equals(redirectType)){
            return this;
        }

        // 로그인이면 유저 정보랑 provider만 반환
        return RedirectResponse.builder()
                .provider(super.getProvider())
                .userInfo(super.getUserInfo())
                .build();
    }
}

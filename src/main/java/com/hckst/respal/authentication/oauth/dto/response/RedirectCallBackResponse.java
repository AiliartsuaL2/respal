package com.hckst.respal.authentication.oauth.dto.response;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class RedirectCallBackResponse extends RedirectResponse{
    private String accessToken;
    private String refreshToken;


    public RedirectCallBackResponse(UserInfo userInfo, String provider, String accessToken, String refreshToken) {
        super(userInfo,provider);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}

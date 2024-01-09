package com.hckst.respal.config.oauth;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Info {
    private UserInfo providerUserInfo;
    private final String grantType = "authorization_code";
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl ;
    private final String infoUrl ;
    private final String logoutUrl ;

    public void setProviderUserInfo(UserInfo providerUserInfo) {
        this.providerUserInfo = providerUserInfo;
    }

    public UserInfo convert(String response) {
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        UserInfo userInfo = gson.fromJson(response, providerUserInfo.getClass());
        userInfo.init();
        return userInfo;
    }
}

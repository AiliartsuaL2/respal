package com.hckst.respal.oauth.service;

import com.hckst.respal.common.converter.Provider;
import com.hckst.respal.jwt.dto.Token;
import com.hckst.respal.oauth.dto.OAuthJoinDto;
import com.hckst.respal.oauth.info.UserInfo;
import com.hckst.respal.oauth.token.OAuthToken;

public interface OAuthService {
    Token login(String accessToken);
    OAuthToken getAccessToken(String code);
    UserInfo getUserInfo(String accessToken);

    Token join(OAuthJoinDto oAuthJoinDto, String oauthAccessToken, Provider provider);
}

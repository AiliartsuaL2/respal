package com.hckst.respal.authentication.oauth.service;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.authentication.oauth.dto.OAuthJoinDto;
import com.hckst.respal.authentication.oauth.dto.info.UserInfo;
import com.hckst.respal.authentication.oauth.token.OAuthToken;

public interface OAuthService {
    Token login(String accessToken);
    OAuthToken getAccessToken(String code);
    UserInfo getUserInfo(String accessToken);

    Token join(OAuthJoinDto oAuthJoinDto, String oauthAccessToken, Provider provider);
}

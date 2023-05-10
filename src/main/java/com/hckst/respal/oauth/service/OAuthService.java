package com.hckst.respal.oauth.service;

import com.hckst.respal.jwt.dto.Token;
import com.hckst.respal.oauth.info.UserInfo;
import com.hckst.respal.oauth.token.OAuthToken;

public interface OAuthService {
    Token login(String accessToken);
    OAuthToken getAccessToken(String code);
    UserInfo getUserInfo(String accessToken);
}

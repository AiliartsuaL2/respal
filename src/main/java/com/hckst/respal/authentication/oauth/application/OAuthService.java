package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.authentication.oauth.dto.request.OAuthJoinRequestDto;
import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import com.hckst.respal.authentication.oauth.token.OAuthToken;

public interface OAuthService {
    Token login(String accessToken);
    OAuthToken getAccessToken(String code);
    UserInfo getUserInfo(String accessToken);
}

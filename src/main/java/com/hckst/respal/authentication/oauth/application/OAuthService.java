package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;

public interface OAuthService {
    Token checkUser(String email);
    OAuthToken getAccessToken(String code, String client);
    UserInfo getUserInfo(String accessToken);
    Token join(MembersJoinRequestDto membersJoinRequestDto);
    void logout(String accessToken);
}

package com.hckst.respal.authentication.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OAuthJoinDto {
    private String password;
    private String nickname;
    private String oauthAccessToken;
}

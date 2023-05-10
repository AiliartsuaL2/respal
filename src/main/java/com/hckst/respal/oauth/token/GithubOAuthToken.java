package com.hckst.respal.oauth.token;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class GithubOAuthToken extends OAuthToken{
    private String accessToken;
    private int expiresIn;
    private String tokenType;
    private String scope;
    private String refreshToken;
    private String refreshTokenExpiresIn;
}

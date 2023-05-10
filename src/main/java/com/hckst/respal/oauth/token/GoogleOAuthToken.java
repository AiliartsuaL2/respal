package com.hckst.respal.oauth.token;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GoogleOAuthToken extends OAuthToken {
    private String accessToken;
    private int expiresIn;
    private String tokenType;
    private String scope;
    private String refreshToken;
}

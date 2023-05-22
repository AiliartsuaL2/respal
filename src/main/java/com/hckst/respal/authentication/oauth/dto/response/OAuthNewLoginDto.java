package com.hckst.respal.authentication.oauth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthNewLoginDto {
    private String oauthAccessToken;
}

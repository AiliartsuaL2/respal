package com.hckst.respal.authentication.oauth.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OAuthJoinRequestDto {
    private String password;
    private String nickname;
    private String oauthAccessToken;
}

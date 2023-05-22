package com.hckst.respal.authentication.oauth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthJoinResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String membersEmail;
}

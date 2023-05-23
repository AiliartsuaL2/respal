package com.hckst.respal.authentication.jwt.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshAccessTokenResponseDto {
    private String message;
    private String accessToken;
}

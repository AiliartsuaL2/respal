package com.hckst.respal.authentication.jwt.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshAccessTokenRequestDto {
    private String refreshToken;
}

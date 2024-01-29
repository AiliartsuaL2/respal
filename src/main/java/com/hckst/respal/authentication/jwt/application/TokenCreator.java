package com.hckst.respal.authentication.jwt.application;

import com.hckst.respal.authentication.jwt.dto.Token;

public interface TokenCreator {
    Token create(Long memberId);

    String renewAccessToken(String refreshToken);

    Long extractPayload(String accessToken);
}

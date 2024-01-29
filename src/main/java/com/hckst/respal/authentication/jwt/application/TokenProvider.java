package com.hckst.respal.authentication.jwt.application;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenProvider {
    String createAccessToken(String payload);

    String createRefreshToken(String payload);

    String getPayload(String token);

    void validateToken(String token);

    Authentication getAuthentication(String accessToken);

    String resolveToken(HttpServletRequest request);
}

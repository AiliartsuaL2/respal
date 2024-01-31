package com.hckst.respal.authentication.jwt.application;


import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JwtService {
    private final TokenCreator tokenCreator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final String TOKEN_PREFIX = "Bearer ";

    @Transactional
    public Token login(Long memberId) {
        return tokenCreator.create(memberId);
    }

    public RefreshAccessTokenResponseDto renewAccessToken(String refreshToken) {
        refreshToken = refreshToken.replace(TOKEN_PREFIX,""); // Bearer 제거
        return RefreshAccessTokenResponseDto.builder()
                .accessToken(tokenCreator.renewAccessToken(refreshToken))
                .build();
    }

    public Long findMemberId(String accessToken) {
        accessToken = accessToken.replace(TOKEN_PREFIX,""); // Bearer 제거
        return tokenCreator.extractPayload(accessToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
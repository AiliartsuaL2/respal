package com.hckst.respal.authentication.jwt.application;

import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import com.hckst.respal.exception.ErrorMessage;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenCreator implements TokenCreator{

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Token create(Long memberId) {
        String accessToken = tokenProvider.createAccessToken(String.valueOf(memberId));
        String refreshToken = createRefreshToken(memberId);
        return new Token(accessToken, refreshToken);
    }

    private String createRefreshToken(Long memberId) {
        Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByKeyId(memberId);
        if (foundRefreshToken.isPresent()) {
            tokenProvider.validateToken(foundRefreshToken.get().getRefreshToken());
            return foundRefreshToken.get().getRefreshToken();
        }

        String createdRefreshToken = tokenProvider.createRefreshToken(String.valueOf(memberId));
        RefreshToken refreshToken = new RefreshToken(createdRefreshToken, memberId);
        refreshTokenRepository.save(refreshToken);
        return createdRefreshToken;
    }

    @Override
    public String renewAccessToken(String refreshToken) {
        tokenProvider.validateToken(refreshToken);
        Long memberId = Long.valueOf(tokenProvider.getPayload(refreshToken));

        // refresh 토큰 DB 검증
        RefreshToken foundRefreshToken = refreshTokenRepository.findByKeyId(memberId).orElseThrow(
                () -> new JwtException(ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION.getMsg()));
        foundRefreshToken.validateSameRefreshToken(refreshToken);
        return tokenProvider.createAccessToken(String.valueOf(memberId));
    }

    @Override
    public Long extractPayload(String accessToken) {
        tokenProvider.validateToken(accessToken);
        return Long.valueOf(tokenProvider.getPayload(accessToken));
    }
}

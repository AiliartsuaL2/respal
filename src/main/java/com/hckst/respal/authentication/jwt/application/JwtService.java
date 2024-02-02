package com.hckst.respal.authentication.jwt.application;


import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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

    public Token login(Long memberId) {
        validate(memberId, ErrorMessage.NOT_EXIST_MEMBER_ID_EXCEPTION);
        return tokenCreator.create(memberId);
    }

    public RefreshAccessTokenResponseDto renewAccessToken(String refreshToken) {
        validate(refreshToken, ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION);
        refreshToken = refreshToken.replace(TOKEN_PREFIX,""); // Bearer 제거
        return RefreshAccessTokenResponseDto.builder()
                .accessToken(tokenCreator.renewAccessToken(refreshToken))
                .build();
    }

    public Long findMemberId(String accessToken) {
        validate(accessToken, ErrorMessage.NOT_EXIST_ACCESS_TOKEN_EXCEPTION);
        accessToken = accessToken.replace(TOKEN_PREFIX,""); // Bearer 제거
        return tokenCreator.extractPayload(accessToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        validate(refreshToken, ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION);
        refreshTokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    private <T> void validate(T data, ErrorMessage errorMessage) {
        if(ObjectUtils.isEmpty(data)) {
            throw new ApplicationException(errorMessage);
        }
    }
}
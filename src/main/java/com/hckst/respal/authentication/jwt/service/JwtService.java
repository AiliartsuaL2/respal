package com.hckst.respal.authentication.jwt.service;


import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.dto.request.RefreshAccessTokenRequestDto;
import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import com.hckst.respal.exception.jwt.IncorrectRefreshTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JwtService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void login(Token tokenDto){
        RefreshToken refreshToken = RefreshToken.builder()
                .keyId(tokenDto.getMembersEmail())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
        String email = refreshToken.getKeyId();
        if(refreshTokenRepository.existsByKeyId(email)){
            log.info("기존의 존재하는 refresh 토큰 삭제");
            refreshTokenRepository.deleteByKeyId(email);
        }
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> getRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    public RefreshAccessTokenResponseDto validateRefreshToken(RefreshAccessTokenRequestDto requestDto){
        RefreshToken refreshToken = getRefreshToken(requestDto.getRefreshToken()).orElseThrow(
                () -> new IncorrectRefreshTokenException()
        );
        String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken);
        return createRefreshJson(createdAccessToken);
    }

    public RefreshAccessTokenResponseDto createRefreshJson(String accessToken){
        if(accessToken == null){
            throw new IncorrectRefreshTokenException();
        }
        RefreshAccessTokenResponseDto response = RefreshAccessTokenResponseDto.builder()
                .accessToken(accessToken)
                .message("AccessToken이 정상적으로 발급되었습니다.")
                .build();

        return response;
    }
}
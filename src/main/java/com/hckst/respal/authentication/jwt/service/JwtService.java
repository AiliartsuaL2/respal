package com.hckst.respal.authentication.jwt.service;


import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import com.hckst.respal.exception.jwt.IncorrectRefreshTokenException;
import com.hckst.respal.exception.jwt.NotExistRefreshTokenException;
import com.hckst.respal.exception.jwt.NotExistTokenException;
import com.hckst.respal.exception.members.InvalidMembersException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
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
    private final MembersRepository membersRepository;

    private static final String TOKEN_PREFIX = "Bearer ";

    @Transactional
    public void login(Token tokenDto){
        if(tokenDto == null){
            throw new NotExistTokenException();
        }
        RefreshToken refreshToken = RefreshToken.builder()
                .keyId(tokenDto.getMembersId())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
        Members members = membersRepository.findById(refreshToken.getKeyId()).orElseThrow(
                () -> new InvalidMembersException());
        String email = members.getEmail();
        tokenDto.setMembersEmail(email);
        if(refreshTokenRepository.existsByKeyId(tokenDto.getMembersId())){
            log.info("기존의 존재하는 refresh 토큰 삭제");
            refreshTokenRepository.deleteByKeyId(tokenDto.getMembersId());
        }
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> getRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    public RefreshAccessTokenResponseDto validateRefreshToken(String requestRefreshToken){
        requestRefreshToken = requestRefreshToken.replace(TOKEN_PREFIX,""); // Bearer 제거
        RefreshToken refreshToken = getRefreshToken(requestRefreshToken).orElseThrow(
                () -> new IncorrectRefreshTokenException()
        );
        String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken.getRefreshToken());
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
    @Transactional
    public void deleteRefreshToken(String refreshToken){
        refreshToken = refreshToken.replace(TOKEN_PREFIX,""); // Bearer 제거
        RefreshToken storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new NotExistRefreshTokenException()
        );
        refreshTokenRepository.deleteByKeyId(storedRefreshToken.getKeyId());
    }
}
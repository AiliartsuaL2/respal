package com.hckst.respal.authentication.jwt.application;


import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.dto.response.RefreshAccessTokenResponseDto;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
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
            throw new ApplicationException(ErrorMessage.NOT_EXIST_TOKEN_INFO_EXCEPTION);
        }
        RefreshToken refreshToken = RefreshToken.builder()
                .keyId(tokenDto.getMembersId())
                .refreshToken(tokenDto.getRefreshToken())
                .build();
        Members members = membersRepository.findById(refreshToken.getKeyId()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.INVALID_MEMBER_EXCEPTION));
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
                () -> new ApplicationException(ErrorMessage.INCORRECT_REFRESH_TOKEN_EXCEPTION)
        );
        String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken.getRefreshToken());
        return createRefreshJson(createdAccessToken);
    }

    public RefreshAccessTokenResponseDto createRefreshJson(String accessToken){
        if(accessToken == null){
            throw new ApplicationException(ErrorMessage.INCORRECT_REFRESH_TOKEN_EXCEPTION);
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
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION)
        );
        refreshTokenRepository.deleteByKeyId(storedRefreshToken.getKeyId());
    }
}
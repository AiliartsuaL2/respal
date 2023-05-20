package com.hckst.respal.authentication.jwt.service;


import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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

    public Map<String,String> validateRefreshToken(String refreshToken){
        RefreshToken refreshToken1 = getRefreshToken(refreshToken).orElseThrow(
                () -> new NoSuchElementException(ErrorMessage.NOT_EXIST_REFRESHTOKEN.getMsg()));
        String createdAccessToken = jwtTokenProvider.validateRefreshToken(refreshToken1);

        return createRefreshJson(createdAccessToken);
    }
    public Map<String, String> createRefreshJson(String createdAccessToken){

        Map<String, String> map = new HashMap<>();
        if(createdAccessToken == null){
            map.put("errortype", "Forbidden");
            map.put("status", "403");
            map.put("message", "Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.");
            return map;
        }
        //기존에 존재하는 accessToken 제거
        map.put("status", "201");
        map.put("message", "Refresh 토큰을 통한 Access Token 생성이 완료되었습니다.");
        map.put("accessToken", createdAccessToken);
        return map;
    }
}
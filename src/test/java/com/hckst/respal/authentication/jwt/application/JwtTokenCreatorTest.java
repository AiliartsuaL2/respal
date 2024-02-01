package com.hckst.respal.authentication.jwt.application;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class JwtTokenCreatorTest {
    @Autowired
    TokenCreator tokenCreator;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("토큰 생성 정상 케이스")
    void 토큰_생성() {
        // given
        Long memberId = 1L;

        // when
        Token token = tokenCreator.create(memberId);

        // then
        assertThat(token.getAccessToken().split("\\.")).hasSize(3);
        assertThat(token.getRefreshToken().split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("토큰 생성시 저장된 Refresh token이 만료된 토큰인 경우 예외 발생")
    void 토큰_생성_저장_된_Refresh_token이_만료_토큰인_경우() {
        // given
        Long memberId = 1L;
        JwtTokenProvider tokenProvider = new JwtTokenProvider("secretKey", 0, 0, userDetailsService);
        TokenCreator newTokenCreator = new JwtTokenCreator(tokenProvider, refreshTokenRepository);

        // 만료된 토큰 생성
        newTokenCreator.create(memberId);

        // when & then
        assertThatThrownBy(() -> newTokenCreator.create(memberId))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("액세스 토큰 재발급 정상 케이스")
    void 액세스_토큰_재발급() {
        // given
        Long memberId = 1L;
        Token token = tokenCreator.create(memberId);

        // when
        String accessToken = tokenCreator.renewAccessToken(token.getRefreshToken());

        // then
        assertThat(accessToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("페이로드 추출 정상 케이스")
    void 페이로드_추출() {
        // given
        Long memberId = 1L;
        Token token = tokenCreator.create(memberId);

        // when
        Long payload = tokenCreator.extractPayload(token.getAccessToken());

        // then
        assertThat(payload).isEqualTo(memberId);
    }
}

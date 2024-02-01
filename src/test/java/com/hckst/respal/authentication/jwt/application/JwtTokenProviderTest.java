package com.hckst.respal.authentication.jwt.application;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
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

class JwtTokenProviderTest {
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    UserDetailsService userDetailsService;

    private static final String PAYLOAD = "payload";

    @Test
    @DisplayName("액세스 토큰 생성 정상 케이스")
    void 액세스_토큰_생성() {
        // given & when
        String accessToken = tokenProvider.createAccessToken(PAYLOAD);
        // then
        // header, payload, signature
        assertThat(accessToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 정상 케이스")
    void 리프레시_토큰_생성() {
        // given & when
        String refreshToken = tokenProvider.createRefreshToken(PAYLOAD);
        // then
        // header, payload, signature
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("토큰의 페이로드 조회 정상 케이스")
    void 토큰_페이로드_조회() {
        // given
        String accessToken = tokenProvider.createAccessToken(PAYLOAD);

        // when
        String payload = tokenProvider.getPayload(accessToken);

        // then
        assertThat(payload).isEqualTo(PAYLOAD);
    }

    @Test
    @DisplayName("액세스 토큰 만료시 예외 발생")
    void 액세스_토큰_만료_시_예외_발생() {
        // given
        TokenProvider tokenProvider = new JwtTokenProvider("secretKey", 0, 0, userDetailsService);
        String accessToken = tokenProvider.createAccessToken(PAYLOAD);

        // when & then
        assertThatThrownBy(() -> tokenProvider.validateToken(accessToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("리프레시 토큰 만료시 예외 발생")
    void 리프레시_토큰_만료_시_예외_발생() {
        // given
        TokenProvider tokenProvider = new JwtTokenProvider("secretKey", 0, 0, userDetailsService);
        String refreshToken = tokenProvider.createRefreshToken(PAYLOAD);

        // when & then
        assertThatThrownBy(() -> tokenProvider.validateToken(refreshToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 시 예외 발생")
    void 유효하지_않은_토큰_검증_시_예외_발생() {
        // given
        String malformedToken = "malFormedToken";

        // when & then
        assertThatThrownBy(() -> tokenProvider.validateToken(malformedToken))
                .isInstanceOf(MalformedJwtException.class);
    }
}
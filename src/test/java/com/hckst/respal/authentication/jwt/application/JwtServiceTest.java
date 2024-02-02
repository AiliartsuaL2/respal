package com.hckst.respal.authentication.jwt.application;

import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class JwtServiceTest {
    @Autowired
    JwtService jwtService;

    @Autowired
    TokenCreator tokenCreator;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Nested
    @DisplayName("로그인 테스트")
    class Login {
        @Test
        @DisplayName("ID에 해당하는 토큰을 발급한다.")
        void ID에_해당하는_토큰을_발급한다() {
            // given
            long memberId = 0L;

            // when
            Token token = jwtService.login(memberId);

            // then
            Long memberIdByAccessToken = jwtService.findMemberId(token.getAccessToken());
            Long memberIdByRefreshToken = jwtService.findMemberId(token.getRefreshToken());
            assertThat(memberIdByAccessToken).isEqualTo(memberId);
            assertThat(memberIdByRefreshToken).isEqualTo(memberId);
            assertThat(token.getAccessToken().split("\\.")).hasSize(3);
            assertThat(token.getRefreshToken().split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("회원 ID가 존재하지 않는경우 예외 발생")
        void 회원_ID가_존재하지_않는경우_예외_발생() {
            // given
            Long memberId = null;

            // when & then
            Assertions.assertThatThrownBy(() -> jwtService.login(memberId))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_MEMBER_ID_EXCEPTION.getMsg());
        }
    }

    @Nested
    @DisplayName("액세스 토큰 Refresh 테스트")
    class RenewAccessToken {
        @Test
        @DisplayName("액세스 토큰에 해당하는 Access Token을 재발급한다.")
        void 리프레시_토큰에_해당하는_Access_Token을_재발급한다() {
            // given
            Long memberId = 0L;
            Token token = tokenCreator.create(memberId);

            // when
            String accessToken = jwtService.renewAccessToken(token.getRefreshToken()).getAccessToken();
            Long memberIdByAccessToken = jwtService.findMemberId(accessToken);

            // then
            assertThat(memberIdByAccessToken).isEqualTo(memberId);
            assertThat(accessToken.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("Refresh Token이 존재하지 않는경우 예외 발생")
        void Refresh_Token이_존재하지_않는경우_예외_발생() {
            // given
            String refreshToken = null;

            // when & then
            Assertions.assertThatThrownBy(() -> jwtService.renewAccessToken(refreshToken))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION.getMsg());
        }
    }

    @Nested
    @DisplayName("액세스 토큰으로 회원 ID 조회 테스트")
    class FindMemberId {
        @Test
        @DisplayName("정상 케이스")
        void 리프레시_토큰에_해당하는_Access_Token을_재발급한다() {
            // given
            Long memberId = 0L;
            Token token = tokenCreator.create(memberId);

            // when
            Long memberIdByAccessToken = jwtService.findMemberId(token.getAccessToken());

            // then
            assertThat(memberIdByAccessToken).isEqualTo(memberId);
        }

        @Test
        @DisplayName("Access Token이 존재하지 않는경우 예외 발생")
        void Access_token이_존재하지_않는경우_예외_발생() {
            // given
            String accessToken = null;

            // when & then
            Assertions.assertThatThrownBy(() -> jwtService.findMemberId(accessToken))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_ACCESS_TOKEN_EXCEPTION.getMsg());
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class Logout {
        @Test
        @DisplayName("로그아웃시 Refresh Token이 제거된다.")
        void 로그아웃시_Refresh_Token이_제거된다() {
            // given
            Long memberId = 0L;
            Token token = tokenCreator.create(memberId);
            String refreshToken = token.getRefreshToken();

            // when
            jwtService.logout(refreshToken);

            // then
            Optional<RefreshToken> foundRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
            assertThat(foundRefreshToken).isEmpty();
        }

        @Test
        @DisplayName("Refresh token이 존재하지 않는 경우 예외 발생")
        void Refresh_token이_존재하지_않는_경우_예외_발생() {
            // given
            String refreshToken = null;

            // when & then
            assertThatThrownBy(() -> jwtService.logout(refreshToken))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION.getMsg());
        }
    }
}
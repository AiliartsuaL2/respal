package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.jwt.application.JwtTokenCreator;
import com.hckst.respal.authentication.jwt.application.TokenCreator;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthRepository;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import com.hckst.respal.config.oauth.OAuthConfig;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.exception.oauth.OAuthLoginException;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class OAuthServiceTest {
    @Autowired
    OAuthConfig oAuthConfig;
    @Autowired
    JwtService jwtService;

    private static final Provider GOOGLE = Provider.GOOGLE;
    private static final Client WEB_PROD = Client.WEB_PROD;
    private static final Client WEB_STAGING = Client.WEB_STAGING;
    private static final Client WEB_DEV = Client.WEB_DEV;
    private static final Client APP = Client.APP;
    private static final String CODE = "code";
    private static final String INVALID_CODE = "invalidCode";
    private static final String EMAIL = "email@gmail.com";
    private static final String INVALID_EMAIL = "invalidEmail@gmail.com";
    private static final String UID = "UID123456789";
    private static final Long ID = 0L;

    @Nested
    @DisplayName("웹 로그인 테스트")
    class Login {
        private OAuthServiceImpl oAuthService;

        @BeforeEach
        public void init() {
            MembersOAuthDto membersOAuth = new MembersOAuthDto(ID, EMAIL);
            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(EMAIL);
            UserInfo invalidUserInfo = new UserInfo();
            invalidUserInfo.setEmail(INVALID_EMAIL);

            MembersRepository membersRepository = mock(MembersRepository.class);
            OauthTmpRepository oauthTmpRepository = spy(OauthTmpRepository.class);
            OauthRepository oauthRepository = mock(OauthRepository.class);

            when(membersRepository.findMembersOauthForLogin(EMAIL, GOOGLE)).thenReturn(Optional.of(membersOAuth));

            oAuthService = spy(new OAuthServiceImpl(oAuthConfig, membersRepository, oauthTmpRepository, oauthRepository, jwtService));
            doReturn(userInfo).when(oAuthService).getUserInfo(GOOGLE, WEB_PROD, CODE);
            doReturn(invalidUserInfo).when(oAuthService).getUserInfo(GOOGLE, WEB_PROD, INVALID_CODE);
        }

        @Test
        @DisplayName("가입된 회원의 경우 토큰을 응답 한다")
        void 가입된_회원의_경우_토큰을_응답_한다() {
            // given
            String code = CODE;

            // when
            Token token = oAuthService.login(GOOGLE, WEB_PROD, code, UID);

            // then
            assertThat(token.getAccessToken().split("\\.").length).isEqualTo(3);
            assertThat(token.getRefreshToken().split("\\.").length).isEqualTo(3);
            assertThat(jwtService.findMemberId(token.getAccessToken())).isEqualTo(ID);
        }

        @Test
        @DisplayName("가입 되지 않은 회원의 경우 로그인 예외를 발생시킨다.")
        void 가입_되지_않은_회원의_경우_로그인_예외를_발생시킨다() {
            // given & when
            String code = INVALID_CODE;

            // then
            assertThatThrownBy(() -> oAuthService.login(GOOGLE, WEB_PROD, code, UID))
                    .isInstanceOf(OAuthLoginException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION.getMsg());
        }
    }

}
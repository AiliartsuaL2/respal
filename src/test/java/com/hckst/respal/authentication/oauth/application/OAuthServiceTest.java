package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.jwt.application.JwtTokenCreator;
import com.hckst.respal.authentication.jwt.application.JwtTokenProvider;
import com.hckst.respal.authentication.jwt.application.UserDetailsServiceImpl;
import com.hckst.respal.authentication.jwt.domain.RefreshToken;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.repository.RefreshTokenRepository;
import com.hckst.respal.authentication.oauth.domain.RedirectType;
import com.hckst.respal.authentication.oauth.domain.repository.OauthRepository;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import com.hckst.respal.config.oauth.OAuthConfig;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.exception.oauth.OAuthLoginException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OAuthServiceTest {
    @Autowired
    OAuthConfig oAuthConfig;
    @Autowired
    JwtService jwtService;
    @Autowired
    OAuthService oAuthService;
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    private static final Provider COMMON = Provider.COMMON;
    private static final Provider GOOGLE = Provider.GOOGLE;
    private static final Provider GITHUB = Provider.GITHUB;
    private static final Client WEB_PROD = Client.WEB_PROD;
    private static final Client WEB_STAGING = Client.WEB_STAGING;
    private static final Client WEB_DEV = Client.WEB_DEV;
    private static final Client APP = Client.APP;
    private static final String CODE = "code";
    private static final String INVALID_CODE = "invalidCode";
    private static final String EMAIL = "email@gmail.com";
    private static final String NICK_NAME = "nickname";
    private static final String PASSWORD = "password";
    private static final String INVALID_EMAIL = "invalidEmail@gmail.com";
    private static final String UID = "UID123456789";
    private static final Long ID = 0L;

    @Nested
    @DisplayName("웹 로그인 테스트")
    class WebLogin {
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
            String redirectUrl = WEB_PROD.getUidRedirectUrl(RedirectType.SIGN_UP, UID);

            // then
            assertThatThrownBy(() -> oAuthService.login(GOOGLE, WEB_PROD, code, UID))
                    .isInstanceOf(OAuthLoginException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION.getMsg())
                    .hasFieldOrPropertyWithValue("redirectUrl", URI.create(redirectUrl));
        }
    }

    @Nested
    @DisplayName("앱 로그인 테스트")
    class AppLogin {
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
            doReturn(userInfo).when(oAuthService).getUserInfo(GOOGLE, APP, CODE);
            doReturn(invalidUserInfo).when(oAuthService).getUserInfo(GOOGLE, APP, INVALID_CODE);
        }

        @Test
        @DisplayName("가입된 회원의 경우 토큰을 응답 한다")
        void 가입된_회원의_경우_토큰을_응답_한다() {
            // given
            String code = CODE;

            // when
            Token token = oAuthService.login(GOOGLE, APP, code, UID);

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
            String redirectUrl = APP.getUidRedirectUrl(RedirectType.SIGN_UP, UID);

            // then
            assertThatThrownBy(() -> oAuthService.login(GOOGLE, APP, code, UID))
                    .isInstanceOf(OAuthLoginException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION.getMsg())
                    .hasFieldOrPropertyWithValue("redirectUrl", URI.create(redirectUrl));
        }
    }

    @Nested
    @DisplayName("OAuth 회원가입 테스트")
    class OAuthJoin {
        @Test
        @DisplayName("정상 케이스의 경우 OAuth와 Member 모두 저장한다.")
        void 정상_케이스의_경우_OAuth와_Member_모두_저장한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(GOOGLE.getValue())
                    .build();

            // when
            oAuthService.join(dto);
            Optional<MembersOAuthDto> members = membersRepository.findMembersOauthForLogin(EMAIL, GOOGLE);

            // then
            assertThat(members).isPresent();
        }

        @Test
        @DisplayName("같은 provider,email 중복 가입의 경우 예외가 발생한다.")
        void 다른_provider_email_중복_가입의_경우_정상_가입_처리() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(GOOGLE.getValue())
                    .build();
            oAuthService.join(dto);

            dto.setProvider(GITHUB.getValue());

            // when
            oAuthService.join(dto);

            Optional<MembersOAuthDto> googleMember = membersRepository.findMembersOauthForLogin(EMAIL, GITHUB);
            Optional<MembersOAuthDto> githubMembers = membersRepository.findMembersOauthForLogin(EMAIL, GOOGLE);

            // then
            assertThat(googleMember).isPresent();
            assertThat(githubMembers).isPresent();
        }

        @Test
        @DisplayName("같은 provider,email 중복 가입의 경우 예외가 발생한다.")
        void 같은_provider_email_중복_가입의_경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(GOOGLE.getValue())
                    .build();
            oAuthService.join(dto);

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("같은 이메일 일반회원 가입 후 같은 이메일의 OAuth 가입의 경우 예외가 발생한다.")
        void 같은_이메일_일반회원_가입_후_같은_이메일의_OAuth_가입의_경우_예외가_발생한다() {
            // given
            // 일반 회원 가입
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(COMMON.getValue())
                    .build();
            oAuthService.join(dto);

            // when & then
            // 같은 이메일로 OAuth 가입
            dto.setProvider(GOOGLE.getValue());

            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("필수 파라미터(이메일)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_이메일_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(GOOGLE.getValue())
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_MEMBER_EMAIL_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("필수 파라미터(비밀번호)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_비밀번호_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .provider(GOOGLE.getValue())
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_PASSWORD_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("필수 파라미터(닉네임)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_닉네임_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .password(PASSWORD)
                    .provider(GOOGLE.getValue())
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_NICKNAME_EXCEPTION.getMsg());
        }


        @Test
        @DisplayName("필수 파라미터(provider)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_provider_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_PROVIDER_TYPE_EXCEPTION.getMsg());
        }
    }

    @Nested
    @DisplayName("일반 회원가입 테스트")
    class CommonJoin {
        @Test
        @DisplayName("정상 케이스")
        void 정상_케이스() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(COMMON.getValue())
                    .build();

            // when
            oAuthService.join(dto);
            Optional<Members> members = membersRepository.findCommonMembersByEmail(EMAIL);

            // then

            assertThat(members).isPresent();
        }

        @Test
        @DisplayName("같은 email의 일반 회원이 존재하는 경우 예외가 발생한다.")
        void 같은_email의_일반_회원이_존재하는_경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(COMMON.getValue())
                    .build();
            oAuthService.join(dto);

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("같은 email의 OAuth 회원이 존재하는 경우 예외가 발생한다.")
        void 같은_email의_OAuth_회원이_존재하는_경우_예외가_발생한다() {
            // given
            // OAuth가입
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(GOOGLE.getValue())
                    .build();
            oAuthService.join(dto);

            dto.setProvider(COMMON.getValue());

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("필수 파라미터(이메일)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_이메일_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .provider(COMMON.getValue())
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_MEMBER_EMAIL_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("필수 파라미터(비밀번호)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_비밀번호_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .provider(COMMON.getValue())
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_PASSWORD_EXCEPTION.getMsg());
        }

        @Test
        @DisplayName("필수 파라미터(닉네임)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_닉네임_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .password(PASSWORD)
                    .provider(COMMON.getValue())
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_NICKNAME_EXCEPTION.getMsg());
        }


        @Test
        @DisplayName("필수 파라미터(provider)가 없는경우 예외가 발생한다.")
        void 필수_파라미터_provider_가_없는경우_예외가_발생한다() {
            // given
            MembersJoinRequestDto dto = MembersJoinRequestDto.builder()
                    .email(EMAIL)
                    .nickname(NICK_NAME)
                    .password(PASSWORD)
                    .build();

            // when & then
            assertThatThrownBy(() -> oAuthService.join(dto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage(ErrorMessage.NOT_EXIST_PROVIDER_TYPE_EXCEPTION.getMsg());
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class Logout {
        private static final String SECRET_KEY = "secretKey";
        @Test
        @DisplayName("로그아웃시 RefreshToken이 제거된다.")
        void 로그아웃시_RefreshToken이_제거된다() {
            // given
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, 1000000L, 1000000L, new UserDetailsServiceImpl(membersRepository));
            Long memberId = 0L;

            String token = jwtTokenProvider.createRefreshToken(String.valueOf(memberId));
            RefreshToken refreshToken = new RefreshToken(token, memberId);
            refreshTokenRepository.save(refreshToken);

            // when
            oAuthService.logout(token);

            // then
            assertThat(refreshTokenRepository.findByRefreshToken(token)).isEmpty();
        }
    }
}
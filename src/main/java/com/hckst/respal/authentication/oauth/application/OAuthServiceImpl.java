package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.RedirectType;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.exception.oauth.OAuthAppLoginException;
import com.hckst.respal.members.application.MembersService;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.members.presentation.dto.response.MembersLoginResponseDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;


@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl {
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final GithubOAuthService githubOAuthService;
    private final MembersService membersService;
    private final OauthTmpRepository oauthTmpRepository;
    private final JwtService jwtService;

    private static final String OAUTH_REDIRECT_URI_PREFIX = "https://api.respal.me";

    private Optional<Token> checkUser(Provider provider, String email) {
        OAuthService oAuthService = getOAuthService(provider);
        return Optional.ofNullable(oAuthService.checkUser(email));
    }

    private Optional<OAuthToken> getAccessToken(Provider provider, String code, String redirectUrl) {
        if(code == null){
            throw new ApplicationException(ErrorMessage.NO_SUCH_OAUTH_CODE_EXCEPTION);
        }
        OAuthService oAuthService = getOAuthService(provider);

        return Optional.ofNullable(oAuthService.getAccessToken(code, redirectUrl));
    }

    private Optional<UserInfo> getUserInfo(Provider provider, String accessToken) {
        OAuthService oAuthService = getOAuthService(provider);
        return Optional.ofNullable(oAuthService.getUserInfo(accessToken));
    }

    public MembersLoginResponseDto join(Provider provider, MembersJoinRequestDto membersJoinRequestDto) {
        Token token = createByProvider(provider, membersJoinRequestDto);
        jwtService.login(token); // refresh 토큰 초기화

        return MembersLoginResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .grantType(token.getGrantType())
                .build();
    }
    public Token createByProvider(Provider provider, MembersJoinRequestDto membersJoinRequestDto) {
        if(Provider.COMMON.equals(provider)){ // 일반 로그인
            return membersService.join(membersJoinRequestDto);
        }
        OAuthService oAuthService = getOAuthService(provider);
        return oAuthService.join(membersJoinRequestDto);
    }

    public Token login(Provider provider, Client client, String code, String uid) {
        String redirectUri = String.join("/", OAUTH_REDIRECT_URI_PREFIX
                , "oauth"
                , client.getEnvironment()
                , "login"
                , provider.getValue());
        OAuthToken oAuthToken = getAccessToken(provider, code, redirectUri).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_TOKEN_INFO_EXCEPTION));
        UserInfo userInfo = getUserInfo(provider, oAuthToken.getAccessToken()).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NOT_EXIST_TOKEN_INFO_EXCEPTION));

        // 신규 회원인경우, email, nickname, image oauth_tmp에 저장 후 redirect
        OauthTmp oauthTmp = OauthTmp.builder()
                .uid(uid)
                .provider(provider)
                .userInfo(userInfo)
                .build();

        // 신규유저 확인
        Token token = checkNewUser(client, oauthTmp);

        // 기존 회원인 경우
        jwtService.login(token); // refresh 토큰 초기화

        // 로그인 성공시 응답
        oauthTmp.addToken(token);
        oauthTmpRepository.save(oauthTmp);

        return token;
    }

    private OAuthService getOAuthService(Provider provider) {
        if(Provider.KAKAO.equals(provider)) {
            return kakaoOAuthService;
        }
        if(Provider.GOOGLE.equals(provider)) {
            return googleOAuthService;
        }
        if(Provider.GITHUB.equals(provider)) {
            return githubOAuthService;
        }
        throw new ApplicationException(ErrorMessage.NOT_EXIST_PROVIDER_TYPE_EXCEPTION);
    }

    private Token checkNewUser(Client client, OauthTmp oauthTmp) {
        // 기존 유저인 경우 토큰반환
        Optional<Token> token = checkUser(oauthTmp.getProvider(), oauthTmp.getUserInfo().getEmail());
        if(token.isPresent()) {
            return token.get();
        }

        log.info("image : " + oauthTmp.getUserInfo().getImage());

        // 신규 유저인 경우 데이터 저장
        oauthTmpRepository.save(oauthTmp);

        // redirect
        String redirectUrl = client.getUidRedirectUrl(RedirectType.SIGN_UP, oauthTmp.getUid());
        URI redirectUri = URI.create(redirectUrl);
        throw new OAuthAppLoginException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION, oauthTmp.getUid(),redirectUri);
    }

    public void logout(String refreshToken) {
        if(refreshToken == null){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION);
        }
        jwtService.deleteRefreshToken(refreshToken);
    }
}

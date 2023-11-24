package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.exception.oauth.OAuthAppLoginException;
import com.hckst.respal.exception.oauth.OAuthWebLoginException;
import com.hckst.respal.members.application.MembersService;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;


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

    private static final String OAUTH_SIGNUP_APP_SCHEME = "app://signup?uid=";

    private Token checkUser(Provider provider, UserInfo userInfo) {
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.checkUser(userInfo);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.checkUser(userInfo);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.checkUser(userInfo);
        }
        return null;
    }

    private OAuthToken getAccessToken(Provider provider, String code, String redirectUrl) {
        if(code == null){
            throw new ApplicationException(ErrorMessage.NO_SUCH_OAUTH_CODE_EXCEPTION);
        }
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.getAccessToken(code, redirectUrl);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.getAccessToken(code, redirectUrl);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.getAccessToken(code, redirectUrl);
        }
        return null;
    }

    private UserInfo getUserInfo(Provider provider, String accessToken) {
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.getUserInfo(accessToken);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.getUserInfo(accessToken);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.getUserInfo(accessToken);
        }
        return null;
    }

    public Token join(Provider provider, MembersJoinRequestDto membersJoinRequestDto) {
        if(Provider.COMMON.equals(provider)){ // 일반 로그인
            return membersService.joinMembers(membersJoinRequestDto);
        }
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.join(membersJoinRequestDto);
        }
        if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.join(membersJoinRequestDto);
        }
        if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.join(membersJoinRequestDto);
        }
        return null;
    }

    public Token login(Provider provider, Client client, String code, String redirectUri, String uid) {
        OAuthToken oAuthToken = getAccessToken(provider, code, redirectUri);
        UserInfo userInfo = getUserInfo(provider, oAuthToken.getAccessToken());
        Token token = checkUser(provider, userInfo);

        // 신규 회원인경우, email, nickname, image oauth_tmp에 저장 후 redirect
        OauthTmp.OauthTmpBuilder oauthTmpBuilder = OauthTmp.builder()
                .uid(uid)
                .provider(provider)
                .userInfo(userInfo);

        // 신규유저 확인
        checkNewUser(token, client, oauthTmpBuilder);

        // 기존 회원인 경우
        jwtService.login(token); // refresh 토큰 초기화

        // 로그인 성공시 응답
        OauthTmp oauthTmpData = oauthTmpBuilder
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
        oauthTmpRepository.save(oauthTmpData);

        return token;
    }

    private void checkNewUser(Token token, Client client, OauthTmp.OauthTmpBuilder oauthTmpBuilder) {
        if(token != null) {
            return;
        }
        OauthTmp oauthTmp = oauthTmpBuilder.build();
        oauthTmpRepository.save(oauthTmp);
        // 앱 요청인경우
        if(Client.APP.equals(client)) {
            URI redirectUrl = URI.create(OAUTH_SIGNUP_APP_SCHEME+oauthTmp.getUid());
            throw new OAuthAppLoginException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION, oauthTmp.getUid(),redirectUrl);
        }
        // 웹 요청인경우
        throw new OAuthWebLoginException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION, oauthTmp.getUid());
    }

    public void logout(String refreshToken) {
        if(refreshToken == null){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION);
        }
        jwtService.deleteRefreshToken(refreshToken);
    }
}

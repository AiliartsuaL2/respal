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
import com.hckst.respal.exception.OAuthLoginException;
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

    // 신규회원
    private static final String SIGNUP_WEB_DEV_REDIRECT_URL = "http://localhost:3000/signup/social?uid=";
    private static final String SIGNUP_WEB_STAGING_REDIRECT_URL = "https://respal-front-staging.vercel.app/signup/social?uid=";
    private static final String SIGNUP_WEB_LIVE_REDIRECT_URL = "https://respal-front-live.vercel.app/signup/social?uid=";
    private static final String SIGNUP_APP_REDIRECT_URL = "app://signup?uid=";
    // 기존회원
    private static final String CALLBACK_WEB_DEV_REDIRECT_URL = "http://localhost:3000/callback?uid=";
    private static final String CALLBACK_WEB_STAGING_REDIRECT_URL = "https://respal-front-staging.vercel.app/callback?uid=";
    private static final String CALLBACK_WEB_LIVE_REDIRECT_URL = "https://respal-front-live.vercel.app/callback?uid=";
    private static final String CALLBACK_APP_REDIRECT_URL = "app://callback?uid=";

    public Token login(Provider provider, UserInfo userInfo) {
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.login(userInfo);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.login(userInfo);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.login(userInfo);
        }
        return null;
    }

    public OAuthToken getAccessToken(Provider provider, String code, String client) {
        if(code == null){
            throw new ApplicationException(ErrorMessage.NO_SUCH_OAUTH_CODE_EXCEPTION);
        }
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.getAccessToken(code, client);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.getAccessToken(code, client);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.getAccessToken(code, client);
        }
        return null;
    }

    public UserInfo getUserInfo(Provider provider, String accessToken) {
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
        }else if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.join(membersJoinRequestDto);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.join(membersJoinRequestDto);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.join(membersJoinRequestDto);
        }
        return null;
    }

    public String login(Provider providerType, UserInfo userInfo, Token token) {
        // 신규 회원인경우, email, nickname, image oauth_tmp에 저장 후 redirect
        String uid = UUID.randomUUID().toString().replace("-", "");
        if(token == null) {
            OauthTmp oauthTmpData = OauthTmp.builder()
                    .uid(uid)
                    .provider(providerType)
                    .userInfo(userInfo)
                    .build();
            oauthTmpRepository.save(oauthTmpData);
            throw new OAuthLoginException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION,uid);
        }
        // 기존 회원인 경우
        jwtService.login(token); // refresh 토큰 초기화
        // 로그인 성공시 응답
        OauthTmp oauthTmpData = OauthTmp.builder()
                .uid(uid)
                .provider(providerType)
                .userInfo(userInfo)
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
        oauthTmpRepository.save(oauthTmpData);
        return uid;
    }

    public void logout(String refreshToken) {
        if(refreshToken == null){
            throw new ApplicationException(ErrorMessage.NOT_EXIST_REFRESH_TOKEN_EXCEPTION);
        }
        jwtService.deleteRefreshToken(refreshToken);
    }
}

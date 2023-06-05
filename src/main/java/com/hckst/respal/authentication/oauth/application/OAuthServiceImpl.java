package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.service.JwtService;
import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.oauth.NoSuchOAuthCodeException;
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
    private static final String SIGNUP_REDIRECT_URL = "http://localhost:3000/signup?endpoint=";
    // 기존회원
    private static final String CALLBACK_REDIRECT_URL = "http://localhost:3000/callback?endpoint=";
    // 로그아웃
    private static final String LOGOUT_REDIRECT_URL = "http://localhost:3000/logout";

    public Token login(Provider provider, UserInfo userInfo, String accessToken) {
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.login(userInfo,accessToken);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.login(userInfo,accessToken);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.login(userInfo,accessToken);
        }
        return null;
    }

    public OAuthToken getAccessToken(Provider provider, String code) {
        if(code == null){
            throw new NoSuchOAuthCodeException();
        }
        if(Provider.KAKAO.equals(provider)){
            return kakaoOAuthService.getAccessToken(code);
        }else if(Provider.GOOGLE.equals(provider)){
            return googleOAuthService.getAccessToken(code);
        }else if(Provider.GITHUB.equals(provider)){
            return githubOAuthService.getAccessToken(code);
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

    public URI getRedirectUrl(Provider providerType, UserInfo userInfo, Token token) {
        // 신규 회원인경우, email, nickname, image oauth_tmp에 저장 후 redirect
        String endpoint = UUID.randomUUID().toString();
        if(token == null){
            OauthTmp oauthTmpData = OauthTmp.builder()
                    .endpoint(endpoint)
                    .provider(providerType)
                    .userInfo(userInfo)
                    .build();
            oauthTmpRepository.save(oauthTmpData);

            return URI.create(SIGNUP_REDIRECT_URL+endpoint);
        }

        // 기존 회원인 경우
        jwtService.login(token); // refresh 토큰 초기화
        // 로그인 성공시 응답
        OauthTmp oauthTmpData = OauthTmp.builder()
                .endpoint(endpoint)
                .provider(providerType)
                .userInfo(userInfo)
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();
        oauthTmpRepository.save(oauthTmpData);

        return URI.create(CALLBACK_REDIRECT_URL+endpoint);
    }

    public URI logout(Provider provider, String accessToken) {
        if(Provider.COMMON.equals(provider)){ // 일반 로그인
            membersService.logout(accessToken);
        }else if(Provider.KAKAO.equals(provider)){
            kakaoOAuthService.logout(accessToken);
        }else if(Provider.GOOGLE.equals(provider)){
            googleOAuthService.logout(accessToken);
        }else if(Provider.GITHUB.equals(provider)){
            githubOAuthService.logout(accessToken);
        }
        return URI.create(LOGOUT_REDIRECT_URL);
    }
}

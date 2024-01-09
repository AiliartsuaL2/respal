package com.hckst.respal.authentication.oauth.application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.RedirectType;
import com.hckst.respal.authentication.oauth.domain.repository.OauthRepository;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.config.oauth.Info;
import com.hckst.respal.config.oauth.OAuthConfig;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.exception.oauth.OAuthAppLoginException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.members.presentation.dto.response.MembersLoginResponseDto;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {
    private final OAuthConfig oAuthConfig;
    private final MembersRepository membersRepository;
    private final OauthTmpRepository oauthTmpRepository;
    private final OauthRepository oauthRepository;
    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String OAUTH_REDIRECT_URI_PREFIX = "https://localhost";

    /**
     * - 가입된 회원인지 확인
     *   - 가입 된 경우
     *      - Token Return
     *   - 가입되지 않은 경우
     *      - 예외처리하여 회원가입 url로 Redirect
     */
    @Override
    public Token login(Provider provider, Client client, String code, String uid) {
        Token token = checkNewUser(provider, client, code, uid);
        jwtService.login(token); // refresh 토큰 초기화
        return token;
    }

    @Override
    public MembersLoginResponseDto join(Provider provider, MembersJoinRequestDto membersJoinRequestDto) {
        duplicationCheckEmail(provider, membersJoinRequestDto.getEmail());
        Members members = Members.create(membersJoinRequestDto);
        membersRepository.save(members);
        if(!Provider.COMMON.equals(provider)) {
            Oauth oauth = new Oauth(members, provider);
            oauthRepository.save(oauth);
        }
        Token token = jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoleType());
        jwtService.login(token); // refresh 토큰 초기화
        return MembersLoginResponseDto.create(token);
    }

    @Override
    public void logout(String refreshToken) {
    }

    /**
     * - 1. code -> accessToken 변환한다.
     * - 2. accessToken을 통해 회원 정보를 가져와서 가입된 회원인지 확인한다. -> Provider + email
     *  - 가입된 회원인 경우
     *      - 회원 정보를 통해서 AccessToken을 받아옴.
     *  - 미가입된 회원인 경우
     *      - 해당 정보들을 OAuth_tmp에 저장 후 uid를 전달하며 redirect
     */
    private Token checkNewUser(Provider provider, Client client, String code, String uid) {
        // OAuth 서버와 통신
        Info info = oAuthConfig.getInfoByProvider(provider);
        String oauthRedirectUri = getOAuthRedirectUri(client.getEnvironment(), provider.getValue());
        OAuthToken accessToken = getAccessToken(info, code, oauthRedirectUri);
        UserInfo userInfo = getUserInfo(info, accessToken.getAccessToken());

        Optional<MembersOAuthDto> membersOAuth = membersRepository.findMembersOauthForLogin(userInfo.getEmail(), provider);
        if(membersOAuth.isPresent()) {
            Members members = membersRepository.findById(membersOAuth.get().getId()).get();
            return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoleType());
        }

        // 미가입 회원시 저장 후 Redirect
        OauthTmp oauthTmp = new OauthTmp(uid, provider, userInfo);
        oauthTmpRepository.save(oauthTmp);

        String redirectUrl = client.getUidRedirectUrl(RedirectType.SIGN_UP, oauthTmp.getUid());
        URI redirectUri = URI.create(redirectUrl);
        throw new OAuthAppLoginException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION, oauthTmp.getUid(),redirectUri);
    }

    private String getOAuthRedirectUri(String environment, String provider) {
        return String.join("/", OAUTH_REDIRECT_URI_PREFIX
                , "oauth"
                , environment
                , "login"
                , provider);
    }

    private OAuthToken getAccessToken(Info providerInfo, String code, String redirectUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(providerInfo.getTokenUrl()) // 요청 할 API Url
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더 설정
                .build();

        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", providerInfo.getGrantType())
                        .queryParam("client_id", providerInfo.getClientId())
                        .queryParam("client_secret", providerInfo.getClientSecret())
                        .queryParam("redirect_uri", redirectUrl)
                        .queryParam("code", code)
                        .build())
                .retrieve() // 데이터 받는 방식, 스프링에서는 exchange는 메모리 누수 가능성 때문에 retrieve 권장
                .bodyToMono(String.class) // Mono 객체로 데이터를 받음 , Mono는 단일 데이터, Flux는 복수 데이터
                .block();// 비동기 방식으로 데이터를 받아옴

        // UnderScoreCase To Camel GsonBuilder,
        if(!response.startsWith("{")) {
            response = "{\"" + response.replace("&", "\",\"").replace("=", "\":\"") + "\"}";
        }
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OAuthToken oAuthToken = gson.fromJson(response, OAuthToken.class);
        return oAuthToken;
    }

    private UserInfo getUserInfo(Info providerInfo, String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl(providerInfo.getInfoUrl()) // 요청 할 API Url
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더 설정
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();
        String response = webClient.get()
                .retrieve() // 데이터 받는 방식, 스프링에서는 exchange는 메모리 누수 가능성 때문에 retrieve 권장
                .bodyToMono(String.class) // Mono 객체로 데이터를 받음 , Mono는 단일 데이터, Flux는 복수 데이터
                .block();// 비동기 방식으로 데이터를 받아옴

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        UserInfo userInfo = gson.fromJson(response, providerInfo.getProviderUserInfo().getClass());
        userInfo.init();
        return userInfo;
    }

    public void duplicationCheckEmail(Provider provider, String email) {
        if(Provider.COMMON.equals(provider)) {
            if(membersRepository.existsMembersByEmail(email)) {
                throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
            }
        }
        if(membersRepository.existsMembersOauthForJoin(email,provider)){
            throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
        }
    }
}

package com.hckst.respal.authentication.oauth.application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hckst.respal.authentication.jwt.application.JwtService;
import com.hckst.respal.authentication.jwt.dto.Token;
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
import com.hckst.respal.global.Utils;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;

import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OAuthServiceImpl implements OAuthService {
    private final OAuthConfig oAuthConfig;
    private final MembersRepository membersRepository;
    private final OauthTmpRepository oauthTmpRepository;
    private final OauthRepository oauthRepository;
    private final JwtService jwtService;
    private static final String OAUTH_REDIRECT_URI_PREFIX = "https://api.respal.me";

    /**
     * - 가입된 회원인지 확인
     *   - 가입 된 경우
     *      - Token Return
     *   - 가입되지 않은 경우
     *      - 예외처리하여 회원가입 url로 Redirect
     */
    @Override
    @Transactional(noRollbackFor = {OAuthAppLoginException.class})
    public Token login(Provider provider, Client client, String code, String uid) {
        // OAuth 서버와 통신
        UserInfo userInfo = getUserInfo(provider, client, code);

        // 저장 후 Redirect
        MembersOAuthDto membersOAuthDto = membersRepository.findMembersOauthForLogin(userInfo.getEmail(), provider)
                .orElseThrow(() -> {
                    OauthTmp oauthTmp = new OauthTmp(uid, provider, userInfo);
                    oauthTmpRepository.save(oauthTmp);

                    String redirectUrl = client.getUidRedirectUrl(RedirectType.SIGN_UP, oauthTmp.getUid());
                    URI redirectUri = URI.create(redirectUrl);
                    return new OAuthAppLoginException(ErrorMessage.NOT_EXIST_MEMBER_EXCEPTION, oauthTmp.getUid(), redirectUri);
                });

        // 기존 회원인 경우 -> token 응답
        return jwtService.login(membersOAuthDto.getId());
    }

    private UserInfo getUserInfo(Provider provider, Client client, String code) {
        Info info = oAuthConfig.getInfoByProvider(provider);
        String oauthRedirectUri = getOAuthRedirectUri(client.getEnvironment(), provider.getValue());
        OAuthToken accessToken = getAccessTokenFromOAuth(info, code, oauthRedirectUri);
        return getUserInfoFromOAuth(info, accessToken.getAccessToken());
    }

    @Override
    @Transactional
    public void join(Provider provider, MembersJoinRequestDto membersJoinRequestDto) {
        checkDuplicatedEmail(provider, membersJoinRequestDto.getEmail());
        Members members = Members.create(membersJoinRequestDto);
        membersRepository.save(members);
        if(!Provider.COMMON.equals(provider)) {
            Oauth oauth = new Oauth(members, provider);
            oauthRepository.save(oauth);
        }
    }

    @Override
    public void logout(String refreshToken) {
    }

    OAuthToken getAccessTokenFromOAuth(Info providerInfo, String code, String redirectUrl) {
        WebClient webClient = WebClient.builder()
                .baseUrl(providerInfo.getTokenUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", providerInfo.getGrantType())
                        .queryParam("client_id", providerInfo.getClientId())
                        .queryParam("client_secret", providerInfo.getClientSecret())
                        .queryParam("redirect_uri", redirectUrl)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return convert(response);
    }

    private UserInfo getUserInfoFromOAuth(Info providerInfo, String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl(providerInfo.getInfoUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();
        String response = webClient.get()
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return providerInfo.convert(response);
    }

    String getOAuthRedirectUri(String environment, String provider) {
        return String.join("/", OAUTH_REDIRECT_URI_PREFIX
                , "oauth"
                , environment
                , "login"
                , provider);
    }

    private OAuthToken convert(String response) {
        String json = Utils.queryParamToJson(response);
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        return gson.fromJson(json, OAuthToken.class);
    }

    public void checkDuplicatedEmail(Provider provider, String email) {
        if(Provider.COMMON.equals(provider)) {
            if(membersRepository.existsMembersByEmail(email)) {
                throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
            }
        }
        if(membersRepository.existsMembersOauthForJoin(email, provider)){
            throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
        }
    }
}

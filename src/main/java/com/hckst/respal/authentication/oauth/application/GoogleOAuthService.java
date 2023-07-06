package com.hckst.respal.authentication.oauth.application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.google.GoogleUserInfo;
import com.hckst.respal.config.OAuthConfig;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.RoleType;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.members.domain.Role;
import com.hckst.respal.authentication.oauth.domain.repository.OauthRepository;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.members.domain.repository.JobRepository;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor

public class GoogleOAuthService implements OAuthService {
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthConfig oAuthConfig;
    private final OauthRepository oauthRepository;
    private final JobRepository jobRepository;

    @Override
    public Token login(UserInfo userInfo) {
        log.info("google login 진입");
        String email = userInfo.getEmail();
        Optional<MembersOAuthDto> membersOauth = membersRepository.findMembersOauthForLogin(email, Provider.GOOGLE);
        if(membersOauth.isPresent()){
            Members members = membersRepository.findById(membersOauth.get().getId()).get();
            return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoles());
        }
        return null;
    }

    @Override
    public OAuthToken getAccessToken(String code, String client) {
        /*
         https://accounts.google.com/o/oauth2/v2/auth?scope=profile&response_type=code
         &client_id="할당받은 id"&redirect_uri="access token 처리")
         로 Redirect URL을 생성하는 로직을 구성
         */
        String redirectUri;
        if(Client.WEB_DEV.getValue().equals(client)){
            redirectUri = oAuthConfig.getGoogle().getWebDevRedirectUri();
        }else if(Client.WEB_STAGING.getValue().equals(client)){
            redirectUri = oAuthConfig.getGoogle().getWebStgRedirectUri();
        }else if(Client.WEB_LIVE.getValue().equals(client)){
            redirectUri = oAuthConfig.getGoogle().getWebLiveRedirectUri();
        }else if(Client.APP.getValue().equals(client)){
            redirectUri = oAuthConfig.getGoogle().getAppRedirectUri();
        } else {
            redirectUri = null;
        }

        WebClient webClient = WebClient.builder()
                .baseUrl(oAuthConfig.getGoogle().getTokenUrl()) // 요청 할 API Url
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더 설정
                .build();

        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", oAuthConfig.getGoogle().getGrantType())
                        .queryParam("client_id", oAuthConfig.getGoogle().getClientId())
                        .queryParam("client_secret", oAuthConfig.getGoogle().getClientSecret())
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build())
                .retrieve() // 데이터 받는 방식, 스프링에서는 exchange는 메모리 누수 가능성 때문에 retrieve 권장
                .bodyToMono(String.class) // Mono 객체로 데이터를 받음 , Mono는 단일 데이터, Flux는 복수 데이터
                .block();// 비동기 방식으로 데이터를 받아옴

        // UnderScoreCase To Camel GsonBuilder,, googleOAuth2Token 객체에 매핑
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OAuthToken oAuthToken = gson.fromJson(response, OAuthToken.class);
        log.info("구글 액세스 토큰 : " + oAuthToken.getAccessToken());

        return oAuthToken;
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl(oAuthConfig.getGoogle().getInfoUrl()) // 요청 할 API Url
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더 설정
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();

        String response = webClient.get()
                .retrieve() // 데이터 받는 방식, 스프링에서는 exchange는 메모리 누수 가능성 때문에 retrieve 권장
                .bodyToMono(String.class) // Mono 객체로 데이터를 받음 , Mono는 단일 데이터, Flux는 복수 데이터
                .block();// 비동기 방식으로 데이터를 받아옴

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        GoogleUserInfo googleUserInfo = gson.fromJson(response , GoogleUserInfo.class);

        UserInfo oAuthUserInfoResponseDto = UserInfo.builder()
                .id(googleUserInfo.getId())
                .email(googleUserInfo.getEmail())
                .image(googleUserInfo.getPicture())
                .nickname(googleUserInfo.getName())
                .build();

        return oAuthUserInfoResponseDto;
    }

    @Override
    public Token join(MembersJoinRequestDto membersJoinRequestDto) {
        // 이미 이메일과 provider로 존재하는경우 exception
        if(membersRepository.existsMembersOauthForJoin(membersJoinRequestDto.getEmail(),Provider.GOOGLE)){
            throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
        }
        Members members = Members.builder()
                .email(membersJoinRequestDto.getEmail())
                .nickname(membersJoinRequestDto.getNickname())
                .role(new Role(RoleType.ROLE_USER))
                .picture(membersJoinRequestDto.getPicture())
                .password(UUID.randomUUID().toString().replace("-", ""))
                .jobId(jobRepository.getReferenceById(membersJoinRequestDto.getJobId()))
                .build();
        Oauth oauth = Oauth.builder()
                .membersId(members)
                .provider(Provider.GOOGLE)
                .build();

        membersRepository.save(members);
        oauthRepository.save(oauth);

        return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoles());
    }

    @Override
    public void logout(String accessToken) {

    }
}

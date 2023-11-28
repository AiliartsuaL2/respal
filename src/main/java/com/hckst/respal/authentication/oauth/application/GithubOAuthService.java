package com.hckst.respal.authentication.oauth.application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.domain.RoleType;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.github.GithubUserInfo;
import com.hckst.respal.config.OAuthConfig;
import com.hckst.respal.authentication.oauth.domain.repository.OauthRepository;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.domain.repository.dto.MembersOAuthDto;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubOAuthService implements OAuthService{
    private final MembersRepository membersRepository;
    private final OauthRepository oauthRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthConfig oAuthConfig;

    @Override
    public Token checkUser(String email) {
        log.info("github login 진입");
        Optional<MembersOAuthDto> membersOauth = membersRepository.findMembersOauthForLogin(email, Provider.GITHUB);
        if(membersOauth.isPresent()){
            Members members = membersRepository.findById(membersOauth.get().getId()).get();
            return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoleType());
        }
        return null;
    }

    @Override
    public OAuthToken getAccessToken(String code, String redirectUrl) {

        WebClient webClient = WebClient.builder()
                .baseUrl(oAuthConfig.getGithub().getTokenUrl()) // 요청 할 API Url
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더 설정
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("client_id", oAuthConfig.getGithub().getClientId())
                        .queryParam("client_secret", oAuthConfig.getGithub().getClientSecret())
                        .queryParam("redirect_uri", redirectUrl)
                        .queryParam("code", code)
                        .build())
                .retrieve() // 데이터 받는 방식, 스프링에서는 exchange는 메모리 누수 가능성 때문에 retrieve 권장
                .bodyToMono(String.class) // Mono 객체로 데이터를 받음 , Mono는 단일 데이터, Flux는 복수 데이터
                .block();// 비동기 방식으로 데이터를 받아옴

        // UnderScoreCase To Camel GsonBuilder,, OAuthToken 객체에 매핑
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OAuthToken oAuthToken = gson.fromJson(response, OAuthToken.class);
        log.info("깃허브 액세스 토큰 : " + oAuthToken.getAccessToken());

        return oAuthToken;
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder()
                .baseUrl(oAuthConfig.getGithub().getInfoUrl()) // 요청 할 API Url
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE) // 헤더 설정
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer "+accessToken)
                .build();

        String response = webClient.get()
                .retrieve() // 데이터 받는 방식, 스프링에서는 exchange는 메모리 누수 가능성 때문에 retrieve 권장
                .bodyToMono(String.class) // Mono 객체로 데이터를 받음 , Mono는 단일 데이터, Flux는 복수 데이터
                .block();// 비동기 방식으로 데이터를 받아옴

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        GithubUserInfo githubUserInfo = gson.fromJson(response, GithubUserInfo.class);

        UserInfo oAuthUserInfoResponseDto = UserInfo.builder()
                .id(githubUserInfo.getId())
                .email(githubUserInfo.getEmail())
                .nickname(githubUserInfo.getLogin())
                .image(githubUserInfo.getAvatar_url())
                .build();

        return oAuthUserInfoResponseDto;
    }

    @Override
    public Token join(MembersJoinRequestDto membersJoinRequestDto) {
        // 이미 이메일과 provider로 존재하는경우 exception
        if(membersRepository.existsMembersOauthForJoin(membersJoinRequestDto.getEmail(),Provider.GITHUB)){
            throw new ApplicationException(ErrorMessage.DUPLICATE_EMAIL_EXCEPTION);
        }
        Members members = Members.builder()
                .email(membersJoinRequestDto.getEmail())
                .nickname(membersJoinRequestDto.getNickname())
                .roleType(RoleType.ROLE_USER)
                .picture(membersJoinRequestDto.getPicture())
                .password(UUID.randomUUID().toString().replace("-", ""))
                .build();
        Oauth oauth = Oauth.builder()
                .membersId(members)
                .provider(Provider.GITHUB)
                .build();

        membersRepository.save(members);
        oauthRepository.save(oauth);

        return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoleType());
    }

    @Override
    public void logout(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //Github 데이터의 경우 json 방식이 invalid json
        // post 헤더에 json 옵션 넣어주기
        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));


        // client id와 client secret을 각각 유저네임과 패스워드로 Authorization 헤더 설정한 후 요청을 보내면 발급받은 액세스 토큰을 지울 수 있다.
        // data: {access_token: accessToken,},
        // auth: { username: CLIENT_ID, password: CLIENT_SECRET, }

//        headers.set(HttpHeaders.AUTHORIZATION,Arrays.asList()); >>
        HttpEntity request = new HttpEntity(headers);

        // Uri 빌더 사용
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(oAuthConfig.getGithub().getLogoutUrl());

        ResponseEntity<String> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.POST,
                request,
                String.class
        );

        // UnderScoreCase To Camel GsonBuilder,, OAuthToken 객체에 매핑
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OAuthToken oAuthToken = gson.fromJson(response.getBody(), OAuthToken.class);
        log.info("깃허브 액세스 토큰 : " + oAuthToken.getAccessToken());
    }
}

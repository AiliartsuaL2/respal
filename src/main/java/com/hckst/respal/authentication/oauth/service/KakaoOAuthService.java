package com.hckst.respal.authentication.oauth.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.RoleType;
import com.hckst.respal.exception.members.DuplicateEmailException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.members.domain.Role;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.oauth.dto.request.OAuthJoinRequestDto;
import com.hckst.respal.authentication.oauth.dto.request.info.KakaoUserInfo;
import com.hckst.respal.config.OAuthConfig;
import com.hckst.respal.authentication.oauth.domain.repository.OAuthRepository;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.members.domain.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService implements OAuthService{

    private final MembersRepository membersRepository;
    private final OAuthRepository oAuthRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthConfig oAuthConfig;

    @Override
    public Token login(String accessToken){
        KakaoUserInfo kakaoUserInfo = getUserInfo(accessToken);
        String email = Optional.ofNullable(kakaoUserInfo.getKakaoAccount().getEmail()).orElse(UUID.randomUUID().toString().replace("-", ""));
        Optional<Members> members = membersRepository.findMembersOauth(email, Provider.KAKAO);
        // 기존 회원인경우 oauthAccessToken 업데이트
        if(members.isPresent()){
            Oauth oauth = oAuthRepository.findOauthByMembersId(members.get());
            oauth.updateAccessToken(accessToken);
        }
        return members.isEmpty() ? null : jwtTokenProvider.createTokenWithRefresh(members.get().getEmail(), members.get().getRoles());
    }

    @Override
    public OAuthToken getAccessToken(String code) {
        // POST 방식으로 key=value 데이터를 요청 (카카오쪽으로)
        // 이 때 필요한 라이브러리가 RestTemplate, 얘를 쓰면 http 요청을 편하게 할 수 있다.
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(headers);

        // Uri 빌더 사용
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(oAuthConfig.getKakao().getTokenUrl())
                .queryParam("grant_type", oAuthConfig.getKakao().getGrantType())
                .queryParam("client_id", oAuthConfig.getKakao().getClientId())
                .queryParam("redirect_uri", oAuthConfig.getKakao().getRedirectUri())
                .queryParam("client_secret", oAuthConfig.getKakao().getClientSecret())
                .queryParam("code", code);

        ResponseEntity<String> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.POST,
                request,
                String.class
        );
        // UnderScoreCase To Camel GsonBuilder,, KakaoOAuth2Token 객체에 매핑
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OAuthToken oAuthToken = gson.fromJson(response.getBody(), OAuthToken.class);
        log.info("카카오 액세스 토큰 : " + oAuthToken.getAccessToken());

        return oAuthToken;
    }

    @Override
    public KakaoUserInfo getUserInfo(String accessToken){
        // 이 때 필요한 라이브러리가 RestTemplate, 얘를 쓰면 http 요청을 편하게 할 수 있다.
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization","Bearer "+accessToken);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                oAuthConfig.getKakao().getInfoUrl(),
                HttpMethod.POST,
                request, // 요청시 보낼 데이터
                String.class // 요청시 반환 데이터 타입
        );
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        KakaoUserInfo kakaoUserInfo = gson.fromJson(response.getBody(), KakaoUserInfo.class);

        return kakaoUserInfo;
    }

    @Override
    public Token join(OAuthJoinRequestDto oAuthJoinRequestDto, String oauthAccessToken, Provider provider) {
        log.info("kakao login 진입");
        String email = getUserInfo(oauthAccessToken).getKakaoAccount().getEmail();
        // 이미 이메일이 존재한다면
        if(membersRepository.findMembersByEmail(email).isPresent()){
            throw new DuplicateEmailException();
        }

        Role role = new Role(RoleType.ROLE_USER);
        Members members = Members.builder()
                .email(email)
                .password(oAuthJoinRequestDto.getPassword())
                .nickname(oAuthJoinRequestDto.getNickname())
                .role(role)
                .build();
        Oauth oauth = Oauth.builder()
                .membersId(members)
                .accessToken(oauthAccessToken)
                .provider(provider)
                .build();
        membersRepository.save(members);
        oAuthRepository.save(oauth);
        return jwtTokenProvider.createTokenWithRefresh(members.getEmail(), members.getRoles());
    }
}

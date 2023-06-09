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
import com.hckst.respal.exception.members.DuplicateEmailException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.members.domain.Role;
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
import org.springframework.web.util.UriComponentsBuilder;

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

    @Override
    public Token login(UserInfo userInfo, String accessToken) {
        log.info("google login 진입");
        String email = userInfo.getEmail();
        Optional<MembersOAuthDto> membersOauth = membersRepository.findMembersOauthForLogin(email, Provider.GOOGLE);

        // 기존 회원인경우 oauthAccessToken 업데이트
        if(membersOauth.isPresent()){
            Members members = membersRepository.findById(membersOauth.get().getId()).get();
            Oauth oauth = oauthRepository.findOauthByMembersId(members);
            oauth.updateAccessToken(accessToken);
            return jwtTokenProvider.createTokenWithRefresh(members.getId(), members.getRoles());
        }
        return null;
    }

    @Override
    public OAuthToken getAccessToken(String code, String client) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity request = new HttpEntity(headers);

        /*
         https://accounts.google.com/o/oauth2/v2/auth?scope=profile&response_type=code
         &client_id="할당받은 id"&redirect_uri="access token 처리")
         로 Redirect URL을 생성하는 로직을 구성
         */
        // Uri 빌더 사용

        String redirectUri = Client.WEB.getValue().equals(client) ? oAuthConfig.getGoogle().getWebRedirectUri() : oAuthConfig.getGoogle().getAppRedirectUri();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(oAuthConfig.getGoogle().getTokenUrl())
                .queryParam("grant_type", oAuthConfig.getGoogle().getGrantType())
                .queryParam("client_id",  oAuthConfig.getGoogle().getClientId())
                .queryParam("client_secret",  oAuthConfig.getGoogle().getClientSecret())
                .queryParam("redirect_uri",  redirectUri)
                .queryParam("code", code);

        ResponseEntity<String> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.POST,
                request,
                String.class
        );

        // UnderScoreCase To Camel GsonBuilder,, googleOAuth2Token 객체에 매핑
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        OAuthToken oAuthToken = gson.fromJson(response.getBody(), OAuthToken.class);
        log.info("구글 액세스 토큰 : " + oAuthToken.getAccessToken());

        return oAuthToken;
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization","Bearer "+accessToken);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                oAuthConfig.getGoogle().getInfoUrl(),
                HttpMethod.GET,
                request, // 요청시 보낼 데이터
                String.class // 요청시 반환 데이터 타입
        );

        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        GoogleUserInfo googleUserInfo = gson.fromJson(response.getBody(), GoogleUserInfo.class);

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
            throw new DuplicateEmailException();
        }
        Members members = Members.builder()
                .email(membersJoinRequestDto.getEmail())
                .nickname(membersJoinRequestDto.getNickname())
                .role(new Role(RoleType.ROLE_USER))
                .picture(membersJoinRequestDto.getPicture())
                .password(UUID.randomUUID().toString().replace("-", ""))
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

package com.hckst.respal.authentication.oauth.application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.RoleType;
import com.hckst.respal.exception.members.DuplicateEmailException;
import com.hckst.respal.members.domain.Members;
import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.members.domain.Role;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.handler.JwtTokenProvider;
import com.hckst.respal.authentication.oauth.dto.request.OAuthJoinRequestDto;
import com.hckst.respal.authentication.oauth.dto.request.info.github.GithubUserInfo;
import com.hckst.respal.config.OAuthConfig;
import com.hckst.respal.authentication.oauth.domain.repository.OAuthRepository;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.members.domain.repository.MembersRepository;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubOAuthService implements OAuthService{
    private final MembersRepository membersRepository;
    private final OAuthRepository oAuthRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthConfig oAuthConfig;

    @Override
    public Token login(UserInfo userInfo, String accessToken) {
        log.info("github login 진입");
        String email = userInfo.getEmail();
        Optional<Members> members = membersRepository.findMembersOauth(email, Provider.GITHUB);
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
        //Github 데이터의 경우 json 방식이 invalid json
        // post 헤더에 json 옵션 넣어주기
        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
        HttpEntity request = new HttpEntity(headers);

        // Uri 빌더 사용
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(oAuthConfig.getGithub().getTokenUrl())
                .queryParam("client_id", oAuthConfig.getGithub().getClientId())
                .queryParam("redirect_uri", oAuthConfig.getGithub().getRedirectUri())
                .queryParam("client_secret", oAuthConfig.getGithub().getClientSecret())
                .queryParam("code", code);

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
                oAuthConfig.getGithub().getInfoUrl(),
                HttpMethod.GET,
                request, // 요청시 보낼 데이터
                String.class // 요청시 반환 데이터 타입
        );
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        GithubUserInfo githubUserInfo = gson.fromJson(response.getBody(), GithubUserInfo.class);

        System.out.println("response = " + response.getBody());
        UserInfo userInfo = UserInfo.builder()
                .id(githubUserInfo.getId())
                .email(githubUserInfo.getEmail())
                .nickname(githubUserInfo.getLogin())
                .image(githubUserInfo.getAvatar_url())
                .build();

        return userInfo;
    }

    @Override
    public Token join(MembersJoinRequestDto membersJoinRequestDto) {
        // 이미 이메일과 provider로 존재하는경우 exception
        if(membersRepository.findMembersOauth(membersJoinRequestDto.getEmail(),Provider.GITHUB).isPresent()){
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
                .provider(Provider.GITHUB)
                .build();

        membersRepository.save(members);
        oAuthRepository.save(oauth);

        return jwtTokenProvider.createTokenWithRefresh(members.getEmail(), members.getRoles());
    }
}
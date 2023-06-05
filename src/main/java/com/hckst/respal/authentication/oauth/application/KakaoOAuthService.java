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
import com.hckst.respal.authentication.oauth.dto.request.info.kakao.KakaoUserInfo;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthService implements OAuthService{

    private final MembersRepository membersRepository;
    private final OauthRepository oauthRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthConfig oAuthConfig;

    @Override
    public Token login(UserInfo userInfo, String accessToken){
        log.info("kakao login 진입");
        String email = userInfo.getEmail();
        Optional<MembersOAuthDto> membersOauth = membersRepository.findMembersOauthForLogin(email, Provider.KAKAO);

        // 기존 회원인경우 oauthAccessToken 업데이트
        if(membersOauth.isPresent()){
            Members members = membersRepository.findById(membersOauth.get().getId()).get();
            Oauth oauth = oauthRepository.findOauthByMembersId(members);
            oauth.updateAccessToken(accessToken);
            return jwtTokenProvider.createTokenWithRefresh(members.getEmail(), members.getRoles());
        }
        return null;
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
    public UserInfo getUserInfo(String accessToken){
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

        UserInfo oAuthUserInfoResponseDto = UserInfo.builder()
                .id(kakaoUserInfo.getId())
                .email(kakaoUserInfo.getKakaoAccount().getEmail())
                .image(kakaoUserInfo.getProperties().getProfileImage())
                .nickname(kakaoUserInfo.getProperties().getNickname())
                .build();

        return oAuthUserInfoResponseDto;
    }

    @Override
    public Token join(MembersJoinRequestDto membersJoinRequestDto) {
        // 일반 이메일이 있는 경우도 exception 처리


        // 이미 이메일과 provider로 존재하는경우 exception
        if(membersRepository.existsMembersOauthForJoin(membersJoinRequestDto.getEmail(),Provider.KAKAO)){
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
                .provider(Provider.KAKAO)
                .build();

        membersRepository.save(members);
        oauthRepository.save(oauth);

        return jwtTokenProvider.createTokenWithRefresh(members.getEmail(), members.getRoles());
    }

    @Override
    public void logout(String accessToken) {

    }
}

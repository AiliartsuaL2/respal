package com.hckst.respal.authentication.oauth.presentation;

import com.hckst.respal.authentication.oauth.dto.response.OAuthJoinResponseDto;
import com.hckst.respal.authentication.oauth.dto.response.OAuthLoginResponseDto;
import com.hckst.respal.authentication.oauth.dto.response.OAuthNewLoginDto;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.dto.ApiErrorResponse;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.service.JwtService;
import com.hckst.respal.authentication.oauth.dto.request.OAuthJoinRequestDto;
import com.hckst.respal.authentication.oauth.service.GithubOAuthService;
import com.hckst.respal.authentication.oauth.service.GoogleOAuthService;
import com.hckst.respal.authentication.oauth.service.KakaoOAuthService;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final GithubOAuthService githubOAuthService;
    private final JwtService jwtService;
    private static final String REDIRECT_URL = "/oauth/join/";

    @GetMapping("/login/{provider}")
    @ResponseBody
    public ResponseEntity<?> oAuthLogin(@PathVariable String provider, String code){
        /**
         *- 기존 회원인 경우
         *  - respal의 accessToken과 refreshToken을 응답해준다.
         *
         * - 신규 회원인 경우
         *   1. 서버 - oauth 인증을 받으면 oauth의 accessToken과 provider(socialType) 정보와 redirectUrl을 보내준다.
         *   2. 클라이언트 - 회원가입 폼에서 닉네임, 비밀번호를 설정 후(email과 profileImage는 oauth에서 받아옴) redirectUrl로 Post 요청을 한다.
         *   3. 서버 - 해당 정보를 db에 저장 후 respal의 accessToken과 refreshToken을 응답해준다.
         */
        Token token = null;
        OAuthToken oAuthToken = null;

        if("kakao".equals(provider)){
            log.info("kakao social login 진입");
            oAuthToken = kakaoOAuthService.getAccessToken(code);
            token = kakaoOAuthService.login(oAuthToken.getAccessToken());
        }
        else if("google".equals(provider)){
            log.info("google social login 진입");
            oAuthToken = googleOAuthService.getAccessToken(code);
            token = googleOAuthService.login(oAuthToken.getAccessToken());
        }
        else if("github".equals(provider)){
            log.info("github social login 진입");
            oAuthToken = githubOAuthService.getAccessToken(code);
            token = githubOAuthService.login(oAuthToken.getAccessToken());
        }

        // 신규 회원인경우 로그인 페이지로 리다이렉트
        if(token == null){
            OAuthNewLoginDto response = OAuthNewLoginDto.builder()
                    .oauthAccessToken(oAuthToken.getAccessToken())
                    .build();
            return ResponseEntity.created(URI.create(REDIRECT_URL+provider)).body(response);
        }

        jwtService.login(token);
        OAuthLoginResponseDto response = OAuthLoginResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .grantType(token.getGrantType())
                .build();

        return ResponseEntity.ok(response);
    }

    // OAuth 회원가입
    @PostMapping("/join/{provider}")
    @ResponseBody
    public ResponseEntity<OAuthJoinResponseDto> oAuthJoin(@PathVariable String provider,
                                                      @RequestBody OAuthJoinRequestDto oAuthJoinRequestDto){
        Token token = null;
        if(Provider.KAKAO.getValue().equals(provider)){
            log.info("kakao social join 진입");
            token = kakaoOAuthService.join(oAuthJoinRequestDto, oAuthJoinRequestDto.getOauthAccessToken(),Provider.KAKAO);
        }
        else if(Provider.GOOGLE.getValue().equals(provider)){
            log.info("google social join 진입");
            token = googleOAuthService.join(oAuthJoinRequestDto, oAuthJoinRequestDto.getOauthAccessToken(), Provider.GOOGLE);
        }
        else if(Provider.GITHUB.getValue().equals(provider)){
            log.info("github social join 진입");
            token = githubOAuthService.join(oAuthJoinRequestDto, oAuthJoinRequestDto.getOauthAccessToken(),Provider.GITHUB);
        }

        OAuthJoinResponseDto response = OAuthJoinResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .grantType(token.getGrantType())
                .build();
        return ResponseEntity.created(URI.create(REDIRECT_URL+provider)).body(response);
    }

}

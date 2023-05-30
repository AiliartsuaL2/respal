package com.hckst.respal.authentication.oauth.presentation;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import com.hckst.respal.authentication.oauth.dto.response.OAuthLoginResponseDto;
import com.hckst.respal.authentication.oauth.dto.response.OAuthNewLoginResponseDto;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.service.JwtService;
import com.hckst.respal.authentication.oauth.dto.request.OAuthJoinRequestDto;
import com.hckst.respal.authentication.oauth.application.GithubOAuthService;
import com.hckst.respal.authentication.oauth.application.GoogleOAuthService;
import com.hckst.respal.authentication.oauth.application.KakaoOAuthService;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.exception.dto.ApiErrorResponse;
import com.hckst.respal.exception.oauth.NoSuchOAuthCodeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원", description = "회원 관련 api")
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final GithubOAuthService githubOAuthService;
    private final JwtService jwtService;

    @Operation(summary = "OAuth 로그인 메서드", description = "OAuth 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = OAuthLoginResponseDto.class))),
            @ApiResponse(responseCode = "307", description = "신규 회원, 회원가입 폼으로 이동", content = @Content(schema = @Schema(implementation = OAuthNewLoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "OAuth code값 없음", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/login/{provider}")
    @ResponseBody
    public ResponseEntity<?> oAuthLogin(@PathVariable String provider, String code){
        /**
         * Todo 서비스단과 리팩터링 하기
         *- 기존 회원인 경우
         *  - respal의 accessToken과 refreshToken을 응답해준다.
         *
         * - 신규 회원인 경우
         *   1. 서버 - oauth 인증을 받으면 oauth의 accessToken과 provider(socialType) 정보와 redirectUrl을 보내준다.
         *   2. 클라이언트 - 회원가입 폼에서 닉네임, 비밀번호를 설정 후(email과 profileImage는 oauth에서 받아옴) redirectUrl로 Post 요청을 한다.
         *   3. 서버 - 해당 정보를 db에 저장 후 respal의 accessToken과 refreshToken을 응답해준다.
         */
        URI redirectUrl = URI.create("http://localhost:3000/signup");
        if(code == null){
            throw new NoSuchOAuthCodeException();
        }
        Token token = null;
        UserInfo userInfo = null;

        if("kakao".equals(provider)){
            log.info("kakao social login 진입");
            OAuthToken oAuthToken = kakaoOAuthService.getAccessToken(code);
            userInfo = kakaoOAuthService.getUserInfo(oAuthToken.getAccessToken());
            token = kakaoOAuthService.login(userInfo, oAuthToken.getAccessToken());
        }
        else if("google".equals(provider)){
            log.info("google social login 진입");
            OAuthToken oAuthToken = googleOAuthService.getAccessToken(code);
            userInfo = googleOAuthService.getUserInfo(oAuthToken.getAccessToken());
            token = googleOAuthService.login(userInfo, oAuthToken.getAccessToken());
        }
        else if("github".equals(provider)){
            log.info("github social login 진입");
            OAuthToken oAuthToken = githubOAuthService.getAccessToken(code);
            userInfo = githubOAuthService.getUserInfo(oAuthToken.getAccessToken());
            token = githubOAuthService.login(userInfo, oAuthToken.getAccessToken());
        }

        // 신규 회원인경우, data 주고 회원가입 url로 요청 받음
        if(token == null){
            OAuthNewLoginResponseDto response = OAuthNewLoginResponseDto.builder()
                    .userInfo(userInfo)
                    .provider(provider)
                    .build();
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).body(response);
        }

        jwtService.login(token); // refresh 토큰 초기화

        // 로그인 성공시 응답
        OAuthLoginResponseDto response = OAuthLoginResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .grantType(token.getGrantType())
                .build();

        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).body(response);
    }
}

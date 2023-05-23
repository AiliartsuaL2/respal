package com.hckst.respal.authentication.oauth.presentation;

import com.hckst.respal.authentication.oauth.dto.response.OAuthJoinResponseDto;
import com.hckst.respal.authentication.oauth.dto.response.OAuthLoginResponseDto;
import com.hckst.respal.authentication.oauth.dto.response.OAuthNewLoginResponseDto;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.jwt.service.JwtService;
import com.hckst.respal.authentication.oauth.dto.request.OAuthJoinRequestDto;
import com.hckst.respal.authentication.oauth.service.GithubOAuthService;
import com.hckst.respal.authentication.oauth.service.GoogleOAuthService;
import com.hckst.respal.authentication.oauth.service.KakaoOAuthService;
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
    private static final String JOIN_REDIRECT_URL = "/oauth/join/";
    private static final String LOGIN_REDIRECT_URL = "/oauth/login/";


    @Operation(summary = "OAuth 로그인 메서드", description = "OAuth 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = OAuthLoginResponseDto.class))),
            @ApiResponse(responseCode = "307", description = "비회원, 회원가입 url 리다이렉트", content = @Content(schema = @Schema(implementation = OAuthNewLoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "OAuth code값 없음", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
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
        if(code == null){
            throw new NoSuchOAuthCodeException();
        }

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
            OAuthNewLoginResponseDto response = OAuthNewLoginResponseDto.builder()
                    .oauthAccessToken(oAuthToken.getAccessToken())
                    .redirectUrl(JOIN_REDIRECT_URL+provider)
                    .build();
            return new ResponseEntity(response, HttpStatus.TEMPORARY_REDIRECT);
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

    @Operation(summary = "OAuth 회원가입 메서드", description = "OAuth 회원가입 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = OAuthJoinResponseDto.class)))
    })
    @PostMapping("/join/{provider}")
    @ResponseBody
    public ResponseEntity<OAuthJoinResponseDto> oAuthJoin(@PathVariable String provider,
                                                      @RequestBody OAuthJoinRequestDto oAuthJoinRequestDto,
                                                          @RequestHeader(value = "Authorization") String oauthAccessToken){
        Token token = null;
        oauthAccessToken = oauthAccessToken.replace("Bearer","");
        if(Provider.KAKAO.getValue().equals(provider)){
            log.info("kakao social join 진입");
            token = kakaoOAuthService.join(oAuthJoinRequestDto, oauthAccessToken, Provider.KAKAO);
        }
        else if(Provider.GOOGLE.getValue().equals(provider)){
            log.info("google social join 진입");
            token = googleOAuthService.join(oAuthJoinRequestDto, oauthAccessToken, Provider.GOOGLE);
        }
        else if(Provider.GITHUB.getValue().equals(provider)){
            log.info("github social join 진입");
            token = githubOAuthService.join(oAuthJoinRequestDto,oauthAccessToken, Provider.GITHUB);
        }

        OAuthJoinResponseDto response = OAuthJoinResponseDto.builder()
                .membersEmail(token.getMembersEmail())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .grantType(token.getGrantType())
                .build();
        return ResponseEntity.created(URI.create(LOGIN_REDIRECT_URL+provider)).body(response);
    }

}

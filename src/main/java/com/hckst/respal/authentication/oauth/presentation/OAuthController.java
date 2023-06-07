package com.hckst.respal.authentication.oauth.presentation;

import com.hckst.respal.authentication.oauth.application.OAuthServiceImpl;
import com.hckst.respal.authentication.oauth.application.OAuthTmpService;
import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.oauth.dto.response.RedirectCallBackResponse;
import com.hckst.respal.authentication.oauth.dto.response.RedirectResponse;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.ProviderConverter;
import com.hckst.respal.exception.dto.ApiErrorResponse;
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

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원", description = "회원 관련 api")
public class OAuthController {
    private final OAuthServiceImpl oAuthService;
    private final OAuthTmpService oAuthTmpService;

    @Operation(summary = "OAuth 로그인 메서드", description = "OAuth 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "307", description = "로그인 및 회원가입 성공, redirect url로 응답"),
            @ApiResponse(responseCode = "400", description = "OAuth code값 없음", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/login/{provider}")
    @ResponseBody
    public ResponseEntity<?> oAuthLogin(@PathVariable String provider, String code, HttpServletRequest request){
        /**
         *- 기존 회원인 경우
         *  - respal의 accessToken과 refreshToken을 응답해준다.
         *
         * - 신규 회원인 경우
         *   1. 서버 - oauth 인증을 받으면 oauth의 accessToken과 provider(socialType) 정보와 redirectUrl을 보내준다.
         *   2. 클라이언트 - 회원가입 폼에서 닉네임, 비밀번호를 설정 후(email과 profileImage는 oauth에서 받아옴) redirectUrl로 Post 요청을 한다.
         *   3. 서버 - 해당 정보를 db에 저장 후 respal의 accessToken과 refreshToken을 응답해준다.
         */
        ProviderConverter providerConverter = new ProviderConverter();
        Provider providerType = providerConverter.convertToEntityAttribute(provider);
        Client client = getClientType(request);

        OAuthToken oAuthToken = oAuthService.getAccessToken(providerType, code, client);
        UserInfo userInfo = oAuthService.getUserInfo(providerType, oAuthToken.getAccessToken());
        Token token = oAuthService.login(providerType, userInfo, oAuthToken.getAccessToken());
        URI redirectUrl = oAuthService.getRedirectUrl(providerType,userInfo,token, client);

        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).build();
    }


    @Operation(summary = "앱용 OAuth 로그인 메서드", description = "앱용 OAuth 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "307", description = "로그인 및 회원가입 성공, redirect url로 응답"),
            @ApiResponse(responseCode = "400", description = "OAuth code값 없음", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/app/login/{provider}")
    @ResponseBody
    public ResponseEntity<?> oAuthAppLogin(@PathVariable String provider, String code){
        /**
         *- 기존 회원인 경우
         *  - respal의 accessToken과 refreshToken을 응답해준다.
         *
         * - 신규 회원인 경우
         *   1. 서버 - oauth 인증을 받으면 oauth의 accessToken과 provider(socialType) 정보와 redirectUrl을 보내준다.
         *   2. 클라이언트 - 회원가입 폼에서 닉네임, 비밀번호를 설정 후(email과 profileImage는 oauth에서 받아옴) redirectUrl로 Post 요청을 한다.
         *   3. 서버 - 해당 정보를 db에 저장 후 respal의 accessToken과 refreshToken을 응답해준다.
         */
        ProviderConverter providerConverter = new ProviderConverter();
        Provider providerType = providerConverter.convertToEntityAttribute(provider);

        OAuthToken oAuthToken = oAuthService.getAccessToken(providerType, code, Client.APP);
        UserInfo userInfo = oAuthService.getUserInfo(providerType, oAuthToken.getAccessToken());
        Token token = oAuthService.login(providerType, userInfo, oAuthToken.getAccessToken());
        URI redirectUrl = oAuthService.getRedirectUrl(providerType,userInfo,token, Client.APP);

        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).build();
    }

    @Operation(summary = "OAuth 정보 요청 메서드", description = "리다이렉트 되며 저장된 OAuth 로그인 및 회원가입 정보를 반환해주는 url 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신규 회원", content = @Content(schema = @Schema(implementation = RedirectResponse.class))),
            @ApiResponse(responseCode = "200", description = "기존 회원", content = @Content(schema = @Schema(implementation = RedirectCallBackResponse.class))),
            @ApiResponse(responseCode = "400", description = "Uid 불일치 및 type 불일치", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/user/{uid}")
    @ResponseBody
    public ResponseEntity<?> requestUserInfo(@PathVariable String uid, @RequestParam String type){
        RedirectResponse response = oAuthTmpService.getOauthTmp(uid,type);
        return ResponseEntity.ok(response);
    }

    // 액세스토큰으로 provider 분기
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(@PathVariable String provider, @RequestHeader String accessToken){
        ProviderConverter providerConverter = new ProviderConverter();
        Provider providerType = providerConverter.convertToEntityAttribute(provider);

        URI redirectUrl = oAuthService.logout(providerType, accessToken, Client.WEB);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).build();
    }

    private Client getClientType(HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");

        // 모바일 기종 체크
        boolean isMobile = userAgent.matches(".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*");

        // IOS_APP, ANDROID_APP 앱 특정 변수(변동)
        if(userAgent.indexOf("IOS_APP") > -1 || userAgent.indexOf("ANDROID_APP") >-1){ // 앱
            return Client.APP;
        }else if(isMobile){ // 모바일 웹
            return Client.WEB;
        }else { // 웹
            return Client.WEB;
        }
    }
}

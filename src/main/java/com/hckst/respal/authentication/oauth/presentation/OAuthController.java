package com.hckst.respal.authentication.oauth.presentation;

import com.hckst.respal.authentication.oauth.application.OAuthServiceImpl;
import com.hckst.respal.authentication.oauth.application.OAuthTmpService;
import com.hckst.respal.authentication.oauth.domain.WebDomain;
import com.hckst.respal.authentication.oauth.presentation.dto.request.info.UserInfo;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectResponse;
import com.hckst.respal.authentication.oauth.token.OAuthToken;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
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
    private static final String OAUTH_LOGIN_APP_SCHEME = "app://callback?uid=";


    /**
     * 클라이언트 : 로그인 후 oauth 서버로부터 받은 code를 queryParam으로 전송
     * 서버 : code값으로 로그인 여부 확인하여 OAuth TMP 테이블에 uid를 기준으로 사용자 정보를 저장 후 return
     * - 앱인경우 커스텀 스킴으로 redicrect 한다.
     */
    @Operation(summary = "OAuth 로그인 메서드", description = "OAuth 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "304", description = "앱 요청시 리다이렉트(커스텀 스킴 호출)",content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "OAuth code값 없음", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{client}/login/{provider}")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<Token>> oAuthLogin(HttpServletRequest request, @PathVariable String client, @PathVariable String provider, String code){
        Client convertedClient = Client.findByValue(client);
        Provider convertedProvider = Provider.findByValue(provider);

        String oauthRedirectUrl = getRedirectUrl(request, convertedClient, convertedProvider);
        log.info(oauthRedirectUrl);

        String uid = UUID.randomUUID().toString();
        Token token = oAuthService.login(convertedProvider, convertedClient, code, oauthRedirectUrl, uid);

        // 앱인경우 커스텀스킴 url로 redirect
        if(Client.APP.equals(convertedClient)){
            URI redirectUrl = URI.create(OAUTH_LOGIN_APP_SCHEME+uid);
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).build();
        }

        // 웹 요청이면 token return
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(token)
                .build();
        return ResponseEntity.ok(response);
    }

    private String getRedirectUrl(HttpServletRequest request, Client client, Provider provider) {
        // 웹 요청인경우, Redirect url을 web 도메인으로 설정
        if(Client.WEB.equals(client)){
            String clientDomain = WebDomain.findDomainByServerName(request.getServerName());
            return String.join("/"
                    ,clientDomain
                    , "oauth"
                    , client.getValue()
                    , "login"
                    , provider.getValue());
        }
        return request.getRequestURL().toString();
    }

    /**
     * 클라이언트 : 로그인 후 oauth 서버로부터 받은 code를 queryParam으로 전송
     * 서버 : code값으로 로그인 여부 확인하여 OAuth TMP 테이블에 uid를 기준으로 사용자 정보를 저장 후 return
     * - 앱인경우 커스텀 스킴으로 redicrect 한다.
     */
    @Operation(summary = "OAuth 로그인 메서드", description = "OAuth 로그인 메서드입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "304", description = "앱 요청시 리다이렉트(커스텀 스킴 호출)",content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "OAuth code값 없음", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{client}/login/{provider}/{code}")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<Token>> oAuthLoginByPath(HttpServletRequest request, @PathVariable String client, @PathVariable String provider, @PathVariable String code){
        Client convertedClient = Client.findByValue(client);
        Provider convertedProvider = Provider.findByValue(provider);

        String oauthRedirectUrl = getRedirectUrl(request, convertedClient, convertedProvider);

        String uid = UUID.randomUUID().toString();
        Token token = oAuthService.login(convertedProvider, convertedClient, code, oauthRedirectUrl, uid);

        // 앱인경우 커스텀스킴 url로 redirect
        if(Client.APP.equals(convertedClient)){
            URI redirectUrl = URI.create(OAUTH_LOGIN_APP_SCHEME+uid);
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUrl).build();
        }

        // 웹 요청이면 token return
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(token)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "OAuth 정보 요청 메서드", description = "리다이렉트 되며 저장된 OAuth 로그인 및 회원가입 정보를 반환해주는 url 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기존 회원, 신규회원에 accessToken, refreshToken 추가", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Uid 불일치 및 type 불일치", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/user/{uid}")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<RedirectResponse>> requestUserInfo(@PathVariable String uid, @RequestParam String type){
        RedirectResponse responseDto = oAuthTmpService.getOauthTmp(uid,type);

        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(responseDto)
                .build();
        return ResponseEntity.ok(response);
    }
}

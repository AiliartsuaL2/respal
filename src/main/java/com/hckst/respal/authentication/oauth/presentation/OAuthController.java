package com.hckst.respal.authentication.oauth.presentation;

import com.hckst.respal.authentication.oauth.application.OAuthService2;
import com.hckst.respal.authentication.oauth.application.OAuthServiceImpl;
import com.hckst.respal.authentication.oauth.application.OAuthTmpService;
import com.hckst.respal.authentication.oauth.domain.RedirectType;
import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectResponse;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.global.dto.ApiCommonResponse;
import com.hckst.respal.global.dto.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.net.URI;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원", description = "회원 관련 api")
public class OAuthController {
    private final OAuthService2 oAuthService;
    private final OAuthTmpService oAuthTmpService;

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
    public ResponseEntity oAuthLogin(
            @Parameter(description = "해당 API를 호출하는 클라이언트 타입입니다.", schema = @Schema(type = "string", allowableValues = {"web-dev","wev-staging","web-prod","app"}))
            @PathVariable String client,
            @Parameter(description = "인증을 받아올 OAuth 서버 타입 입니다.", schema = @Schema(type = "string", allowableValues = {"kakao","google","github"}))
            @PathVariable String provider,
            @Parameter(description = "OAuth 서버로부터 받아오는 code입니다.")
            @RequestParam String code){
        Client convertedClient = Client.findByValue(client);
        Provider convertedProvider = Provider.findByValue(provider);

        String uid = UUID.randomUUID().toString();
        Token token = oAuthService.login(convertedProvider, convertedClient, code, uid);

        String redirectUrl = convertedClient.getUidRedirectUrl(RedirectType.CALL_BACK, uid);

        // 웹요청의경우 쿠키에 토큰 추가
        // 개발 환경 관련 임시로 쿠키가 아닌, Parameter에 추가
        if(! Client.APP.equals(convertedClient)) {
//            ResponseCookie cookie = token.convert(convertedClient);
//            response.addHeader("Set-Cookie", cookie.toString());
            redirectUrl += token.convertToQueryParameter();
        }
        URI redirectUri = URI.create(redirectUrl);

        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    @Operation(summary = "OAuth 정보 요청 메서드", description = "리다이렉트 되며 저장된 OAuth 로그인 및 회원가입 정보를 반환해주는 url 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기존 회원, 신규회원에 accessToken, refreshToken 추가", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Uid 불일치 및 type 불일치", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/user/{uid}")
    @ResponseBody
    public ResponseEntity<ApiCommonResponse<RedirectResponse>> requestUserInfo(
            @Parameter(description = "조회 할 데이터의 UID 입니다.")
            @PathVariable String uid,
            @Parameter(description = "해당 UID를 받아오며 리다이렉트 되었던 타입 입니다.", schema = @Schema(type = "string", allowableValues = {"signup","callback"}))
            @RequestParam String type){
        RedirectResponse responseDto = oAuthTmpService.getOauthTmp(uid,type);
        ApiCommonResponse response = ApiCommonResponse.builder()
                .statusCode(200)
                .result(responseDto)
                .build();
        return ResponseEntity.ok(response);
    }
}

package com.hckst.respal.controller;

import com.google.gson.Gson;
import com.hckst.respal.common.converter.Provider;
import com.hckst.respal.dto.response.ResponseDto;
import com.hckst.respal.jwt.dto.Token;
import com.hckst.respal.jwt.service.JwtService;
import com.hckst.respal.oauth.dto.OAuthJoinDto;
import com.hckst.respal.oauth.service.GithubOAuthService;
import com.hckst.respal.oauth.service.GoogleOAuthService;
import com.hckst.respal.oauth.service.KakaoOAuthService;
import com.hckst.respal.oauth.token.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<ResponseDto> oAuthLogin(@PathVariable String provider, String code){
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
        Gson gson = new Gson();

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
            Map<String, String> map = new HashMap<>();
            map.put("oauthAccessToken",oAuthToken.getAccessToken());
            map.put("redirectUrl",REDIRECT_URL+provider);
            String json = gson.toJson(map);
            ResponseDto response = ResponseDto.builder()
                    .success(true)
                    .code(202)
                    .data(json)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        jwtService.login(token);
        String json = gson.toJson(token);
        ResponseDto response = ResponseDto.builder()
                .success(true)
                .code(200)
                .data(json)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // redirect용 url
    @GetMapping("/join/{provider}")
    public String oAuthJoinRedirect(@PathVariable String provider, Model model, String oauthToken){
        model.addAttribute("oauthToken", oauthToken);
        return "/member/join.html";
    }

    // 가입용 url
    @PostMapping("/join/{provider}")
    @ResponseBody
    public ResponseEntity<ResponseDto> oAuthJoin(@PathVariable String provider,
                            @RequestBody OAuthJoinDto oAuthJoinDto){
        Token token = null;
        if(Provider.KAKAO.getValue().equals(provider)){
            log.info("kakao social join 진입");
            token = kakaoOAuthService.join(oAuthJoinDto,oAuthJoinDto.getOauthAccessToken(),Provider.KAKAO);
        }
        else if(Provider.GOOGLE.getValue().equals(provider)){
            log.info("google social join 진입");
            token = googleOAuthService.join(oAuthJoinDto,oAuthJoinDto.getOauthAccessToken(), Provider.GOOGLE);
        }
        else if(Provider.GITHUB.getValue().equals(provider)){
            log.info("github social join 진입");
            token = githubOAuthService.join(oAuthJoinDto,oAuthJoinDto.getOauthAccessToken(),Provider.GITHUB);
        }

        Gson gson = new Gson();
        jwtService.login(token);
        String json = gson.toJson(token);
        ResponseDto response = ResponseDto.builder()
                .success(true)
                .code(201)
                .data(json)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}

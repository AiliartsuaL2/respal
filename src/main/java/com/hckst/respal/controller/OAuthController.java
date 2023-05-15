package com.hckst.respal.controller;

import com.google.gson.Gson;
import com.hckst.respal.jwt.dto.Token;
import com.hckst.respal.jwt.service.JwtService;
import com.hckst.respal.oauth.service.GithubOAuthService;
import com.hckst.respal.oauth.service.GoogleOAuthService;
import com.hckst.respal.oauth.service.KakaoOAuthService;
import com.hckst.respal.oauth.token.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/login/{socialType}")
    @ResponseBody
    public ResponseEntity<String> oAuthLogin(@PathVariable String socialType, String code){
        Token token = null;
        Gson gson = new Gson();
        // oauth accesstoken을 폼을 넘겨줄때 준다

        if("kakao".equals(socialType)){
            log.info("kakao social login 진입");
            OAuthToken oAuthToken = kakaoOAuthService.getAccessToken(code);
            token = kakaoOAuthService.login(oAuthToken.getAccessToken());
        }
        else if("google".equals(socialType)){
            log.info("google social login 진입");
            OAuthToken oAuthToken = googleOAuthService.getAccessToken(code);
            token = googleOAuthService.login(oAuthToken.getAccessToken());
        }
        else if("github".equals(socialType)){
            log.info("github social login 진입");
            OAuthToken oAuthToken = githubOAuthService.getAccessToken(code);
            token = githubOAuthService.login(oAuthToken.getAccessToken());
        }
        // 존재하지 않는 사용자인경우
        if(token == null){
            Map<String, String> map = new HashMap<>();
            map.put("newMember","true");
            map.put("redirectUrl",REDIRECT_URL+socialType);
            String response = gson.toJson(map);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }
        jwtService.login(token);
        String response = gson.toJson(token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // redirect용 url
    @GetMapping("/join/{socialType}")
    public String oAuthJoinRedirect(@PathVariable String socialType){
        return "/member/join.html";
    }
    // 가입용 url
    @PostMapping("/join/{socialType}")
    @ResponseBody
    public String oAuthJoin(@PathVariable String socialType){
        return "";
    }

}

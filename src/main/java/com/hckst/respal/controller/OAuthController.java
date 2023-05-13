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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final GithubOAuthService githubOAuthService;
    private final JwtService jwtService;

    @GetMapping("/{socialType}/login")
    @ResponseBody
    public String oAuthLogin(@PathVariable String socialType, String code){
        Token token = null;
        Gson gson = new Gson();

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
        jwtService.login(token);
        return gson.toJson(token);
    }
    // redirect용 url
    @GetMapping("/{socialType}/join")
    public String oAuthJoinRedirect(@PathVariable String socialType){
        return "/member/join.html";
    }
    // 가입용 url
    @PostMapping("/{socialType}/join")
    @ResponseBody
    public String oAuthJoin(@PathVariable String socialType){
        return "";
    }

}

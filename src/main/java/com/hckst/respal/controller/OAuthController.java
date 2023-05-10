package com.hckst.respal.controller;

import com.google.gson.Gson;
import com.hckst.respal.jwt.dto.Token;
import com.hckst.respal.jwt.service.JwtService;
import com.hckst.respal.oauth.service.GithubOAuthService;
import com.hckst.respal.oauth.service.GoogleOAuthService;
import com.hckst.respal.oauth.service.KakaoOAuthService;
import com.hckst.respal.oauth.token.GithubOAuthToken;
import com.hckst.respal.oauth.token.GoogleOAuthToken;
import com.hckst.respal.oauth.token.KakaoOAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Slf4j
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final GithubOAuthService githubOAuthService;
    private final JwtService jwtService;

    @GetMapping("/{socialType}/login")
    public String oAuthLogin(@PathVariable String socialType, String code){
        Token token = null;
        Gson gson = new Gson();

        if("kakao".equals(socialType)){
            log.info("kakao social login 진입");
            KakaoOAuthToken kakaoAccessTOken = kakaoOAuthService.getAccessToken(code);
            token = kakaoOAuthService.login(kakaoAccessTOken.getAccessToken());
        }
        else if("google".equals(socialType)){
            log.info("google social login 진입");
            GoogleOAuthToken googleAccessToken = googleOAuthService.getAccessToken(code);
            token = googleOAuthService.login(googleAccessToken.getAccessToken());
        }
        else if("github".equals(socialType)){
            log.info("github social login 진입");
            GithubOAuthToken githubAccessToken = githubOAuthService.getAccessToken(code);
            token = githubOAuthService.login(githubAccessToken.getAccessToken());
        }
        jwtService.login(token);
        return gson.toJson(token);
    }
}

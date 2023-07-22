package com.hckst.respal.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "oauth")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class OAuthConfig {
    private final Social kakao;
    private final Social google;
    private final Social github;

    @RequiredArgsConstructor
    @Getter
    public static class Social {
        private final String grantType = "authorization_code";
        private final String clientId;
        private final String clientSecret;
        private final String tokenUrl ;
        private final String infoUrl ;
        private final String logoutUrl ;
    }
}

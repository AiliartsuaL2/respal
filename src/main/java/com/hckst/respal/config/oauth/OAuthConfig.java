package com.hckst.respal.config.oauth;

import com.hckst.respal.authentication.oauth.presentation.dto.response.info.github.GithubUserInfo;
import com.hckst.respal.authentication.oauth.presentation.dto.response.info.google.GoogleUserInfo;
import com.hckst.respal.authentication.oauth.presentation.dto.response.info.kakao.KakaoUserInfo;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "oauth")
@ConstructorBinding
@Getter
public class OAuthConfig {
    private final Info kakao;
    private final Info google;
    private final Info github;

    public OAuthConfig(Info kakao, Info google, Info github) {
        this.kakao = kakao;
        this.kakao.setProviderUserInfo(new KakaoUserInfo());
        this.google = google;
        this.google.setProviderUserInfo(new GoogleUserInfo());
        this.github = github;
        this.github.setProviderUserInfo(new GithubUserInfo());
    }

    public Info getInfoByProvider(Provider provider) {
        if(Provider.KAKAO.equals(provider)) {
            return kakao;
        }
        if(Provider.GOOGLE.equals(provider)) {
            return google;
        }
        if(Provider.GITHUB.equals(provider)) {
            return github;
        }
        throw new ApplicationException(ErrorMessage.NOT_EXIST_PROVIDER_TYPE_EXCEPTION);
    }
}

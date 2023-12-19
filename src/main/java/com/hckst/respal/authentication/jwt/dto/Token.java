package com.hckst.respal.authentication.jwt.dto;

import com.google.gson.Gson;
import com.hckst.respal.converter.Client;
import java.net.URLEncoder;
import java.util.Base64;
import lombok.*;
import org.springframework.http.ResponseCookie;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private static final String COOKIE_DOMAIN = ".respal.me";

    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long membersId;
    private String membersEmail;

    public ResponseCookie convert(Client convertedClient) {
        String encodedToken = Base64.getEncoder().encodeToString(new Gson().toJson(this).getBytes());
        ResponseCookie cookie = ResponseCookie.from("token", encodedToken)
                .path(convertedClient.getCookiePath())
                .domain(COOKIE_DOMAIN)
                .httpOnly(true)
                .maxAge(3600)
                .build();
        return cookie;
    }

    @Override
    public String toString() {
        return "{" +
                "grantType='" + grantType + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", membersId=" + membersId +
                ", membersEmail='" + membersEmail + '\'' +
                '}';
    }

    public String convertToQueryParameter() {
        String encodedToken = URLEncoder.encode(this.toString());
        return "?token="+encodedToken;
    }
}

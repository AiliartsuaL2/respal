package com.hckst.respal.authentication.jwt.dto;

import com.google.gson.Gson;
import com.hckst.respal.converter.Client;
import java.util.Base64;
import javax.servlet.http.Cookie;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long membersId;
    private String membersEmail;

    public Cookie convert(Client convertedClient) {
        String encodedToken = Base64.getEncoder().encodeToString(new Gson().toJson(this).getBytes());
        Cookie cookie = new Cookie("token", encodedToken);
        cookie.setMaxAge(3600);
        cookie.setPath(convertedClient.getCookiePath());
        return cookie;
    }
}

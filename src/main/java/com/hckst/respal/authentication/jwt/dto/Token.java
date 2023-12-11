package com.hckst.respal.authentication.jwt.dto;

import com.google.gson.Gson;
import com.hckst.respal.converter.Client;
import java.util.Base64;
import lombok.*;
import org.springframework.http.ResponseCookie;

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

    public ResponseCookie convert(Client convertedClient) {
        String encodedToken = Base64.getEncoder().encodeToString(new Gson().toJson(this).getBytes());
        ResponseCookie cookie = ResponseCookie.from("token", encodedToken)
                .path(convertedClient.getCookiePath())
                .sameSite("None")
                .httpOnly(false)
                .secure(true)
                .maxAge(3600)
                .build();
        return cookie;
    }
}

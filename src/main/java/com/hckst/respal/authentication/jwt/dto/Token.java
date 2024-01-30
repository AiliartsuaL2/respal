package com.hckst.respal.authentication.jwt.dto;

import com.google.gson.Gson;
import com.hckst.respal.converter.Client;
import java.net.URLEncoder;
import java.util.Base64;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.ResponseCookie;

@Getter
@AllArgsConstructor
@Schema(description = "토큰")
public class Token {
    @Schema(description = "액세스 토큰")
    private String accessToken;
    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    public String convertToQueryParameter() {
        String encodedToken = URLEncoder.encode(this.toString());
        return "?token="+encodedToken;
    }

    @Override
    public String toString() {
        return "{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}

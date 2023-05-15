package com.hckst.respal.jwt.dto;

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
    private String membersEmail;
}

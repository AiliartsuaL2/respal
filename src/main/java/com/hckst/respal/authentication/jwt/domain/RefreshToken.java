package com.hckst.respal.authentication.jwt.domain;

import com.hckst.respal.authentication.jwt.dto.Token;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;
    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String refreshToken;
    @Column(name = "MEMBERS_ID", nullable = false)
    private Long keyId;

    public static RefreshToken create(Token token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.refreshToken = token.getRefreshToken();
        refreshToken.keyId = token.getMembersId();
        return refreshToken;
    }
}

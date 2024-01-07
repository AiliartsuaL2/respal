package com.hckst.respal.authentication.oauth.domain;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.ProviderConverter;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="OAUTH_TMP")
public class OauthTmp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 쿼리파라미터로 식별하는 uid
    @Column(length = 36)
    private String uid;
    //소셜 타입
    @Convert(converter = ProviderConverter.class)
    @Column(columnDefinition = "varchar(10)")
    private Provider provider;
    // 유저정보
    @Embedded
    private UserInfo userInfo;
    // 액세스 토큰
    @Column(length = 255)
    private String accessToken;
    // 리프레쉬 토큰
    @Column(length = 255)
    private String refreshToken;
    // 등록일자
    private LocalDateTime regTime;

    public OauthTmp(String uid, Provider provider, UserInfo userInfo){
        this.uid = uid;
        this.provider = provider;
        this.userInfo = userInfo;
        this.regTime = LocalDateTime.now();
    }

    public void addToken(Token token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}

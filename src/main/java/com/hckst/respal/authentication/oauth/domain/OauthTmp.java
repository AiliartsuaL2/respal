package com.hckst.respal.authentication.oauth.domain;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.ProviderConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="OAUTH_TMP")
public class OauthTmp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OAUTH_TMP_ID")
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

    @Builder
    public OauthTmp(String uid, Provider provider, UserInfo userInfo, String accessToken, String refreshToken){
        this.uid = uid;
        this.provider = provider;
        this.userInfo = userInfo;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.regTime = LocalDateTime.now();
    }
}

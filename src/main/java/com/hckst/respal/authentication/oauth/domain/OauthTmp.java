package com.hckst.respal.authentication.oauth.domain;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.ProviderConverter;
import com.hckst.respal.members.domain.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="OAUTH_TMP")
public class OauthTmp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OAUTH_TMP_ID")
    private Long id;
    @Column(length = 36)
    private String endPoint;
    //소셜 타입
    @Convert(converter = ProviderConverter.class)
    @Column(columnDefinition = "varchar(10)")
    private Provider provider;
    @Embedded
    private UserInfo userInfo;
    @Column(length = 255)
    private String accessToken;
    @Column(length = 255)
    private String refreshToken;
}

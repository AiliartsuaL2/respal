package com.hckst.respal.authentication.oauth.domain;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.ProviderConverter;
import com.hckst.respal.members.domain.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="OAUTH")
public class Oauth {
    //ouath id
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OAUTH_ID")
    private Long id;

    //회원 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "MEMBERS_ID")
    private Members membersId;

    @Column(length = 255)
    private String accessToken;

    //소셜 타입
    @Convert(converter = ProviderConverter.class)
    @Column(columnDefinition = "varchar(10)")
    private Provider provider;

    public void updateAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

}

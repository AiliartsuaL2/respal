package com.hckst.respal.domain;

import com.hckst.respal.common.converter.Provider;
import com.hckst.respal.common.converter.ProviderConverter;
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

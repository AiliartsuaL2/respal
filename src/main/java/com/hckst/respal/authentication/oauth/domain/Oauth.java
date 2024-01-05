package com.hckst.respal.authentication.oauth.domain;

import com.hckst.respal.converter.Provider;
import com.hckst.respal.converter.ProviderConverter;
import com.hckst.respal.members.domain.Members;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="OAUTH")
public class Oauth {
    //ouath id
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //회원 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "MEMBERS_ID")
    private Members membersId;
    //소셜 타입
    @Convert(converter = ProviderConverter.class)
    @Column(columnDefinition = "varchar(10)")
    private Provider provider;

    public Oauth(Members membersId, Provider provider){
        this.membersId = membersId;
        this.membersId.getOauthList().add(this);
        this.provider = provider;
    }
}

package com.hckst.respal.members.domain;

import com.hckst.respal.authentication.oauth.domain.Oauth;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="MEMBERS")
@AllArgsConstructor
public class Members implements UserDetails {

    // 회원 ID
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBERS_ID")
    private Long id;

    // 이메일
    @Column(length = 50)
    private String email;

    // 비밀번호
    @Column(length = 50)
    private String password;

    // 닉네임
    @Column(length = 20)
    private String nickname;

    // 가입일시
    private LocalDateTime regTime;

    //권한
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(
            name="members_role",
            joinColumns={@JoinColumn(name="MEMBERS_ID", referencedColumnName="MEMBERS_ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ROLE_ID")})
    private List<Role> roles;

    //oauth
    @OneToMany(mappedBy = "membersId")
    private List<Oauth> oauthList;

    @Builder
    public Members(String password, String nickname, String email, Role role){
        this.password = password;
        this.nickname = nickname;
        this.roles = new ArrayList<>();
        this.regTime = LocalDateTime.now();
        this.email = email;
        this.oauthList = new ArrayList<>();
        this.roles.add(role);
    }

    //업데이트 처리 메서드
    public void updateMemberInfo(String password, String nickname){
        this.password = password;
        this.nickname = nickname;
    }


    // 시큐리티 설정
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}

package com.hckst.respal.members.domain;

import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.converter.TFCodeConverter;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

    // 이메일 , 일반 로그인 식별자
    @Column(length = 50)
    private String email;
    // 비밀번호 , 암호화 되어 진행, 결과값은 항상 60
    @Column(length = 60)
    private String password;
    // 닉네임
    @Column(length = 20)
    private String nickname;
    @Column(length = 100)
    private String picture;


    // 비밀번호 재설정 컬럼 Y인경우 재설정 필요
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode passwordTmpYn;

    // 가입일시
    private LocalDateTime regTime;


    /**
     * 연관관계 설정
     */
    // 직업 id, 단방향 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "JOB_ID")
    private Job jobId;

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
    public Members(String password, String nickname, String email, String picture, Role role, Job jobId){
        this.password = encryptPassword(password);
        this.picture = picture;
        this.nickname = nickname;
        this.roles = new ArrayList<>();
        this.regTime = LocalDateTime.now();
        this.email = email;
        this.oauthList = new ArrayList<>();
        this.jobId = jobId;
        this.roles.add(role);
        this.passwordTmpYn = TFCode.FALSE;
    }

    //회원정보 수정
    public void updateMemberInfo(String password, String nickname){
        this.password = encryptPassword(password);
        this.nickname = nickname;
    }

    //비밀번호 수정
    public void updatePassword(String password){
        this.passwordTmpYn = TFCode.FALSE;
        this.password = encryptPassword(password);
    }

    //임시 비밀번호 설정
    public void updateTmpPassword(String password){
        this.passwordTmpYn = TFCode.TRUE;
        this.password = encryptPassword(password);
    }
    public void tmpPasswordToFalse(){
        this.passwordTmpYn = TFCode.FALSE;
    }

    // 비밀번호를 암호화하는 메서드
    public String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(password);
        return encryptedPassword;
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

package com.hckst.respal.members.domain;

import com.hckst.respal.authentication.oauth.domain.Oauth;
import com.hckst.respal.comment.domain.Comment;
import com.hckst.respal.converter.TFCode;
import com.hckst.respal.converter.TFCodeConverter;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.tag.domain.Tag;
import com.hckst.respal.resume.domain.Resume;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="MEMBERS")
@AllArgsConstructor
public class Members implements UserDetails {
    private static String RANDOM_PICTURE_URL = "https://www.gravatar.com/avatar/";
    private static String PICTURE_TYPE_PARAM = "?d=identicon";
    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // 회원 ID
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(length = 2083)
    private String picture;


    // 비밀번호 재설정 컬럼 Y인경우 재설정 필요
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode passwordTmpYn;

    // 가입일시
    private LocalDateTime regTime;

    /**
     * 연관관계 매핑
     * 양방향
     * Resume
     * One to Many
     * 작성한 이력서 리스트
     */
    @OneToMany(mappedBy = "members")
    private List<Resume> resumeList;


    // R2DBC 관련 연관관계 매핑 x
    @Transient
    private List<Comment> commentList;

    /**
     * 연관관계 매핑
     * 양방향
     * Tag
     * One to Many (다대다 중간테이블)
     * 언급당한 Tag 리스트
     */
    @OneToMany(mappedBy = "members")
    private List<Tag> taggedList;

    //권한
    @Enumerated(EnumType.STRING)
    @Column(length = 20,nullable=false)
    private RoleType roleType;

    //oauth
    @OneToMany(mappedBy = "membersId")
    private List<Oauth> oauthList;

    public static Members create(MembersJoinRequestDto membersJoinRequestDto) {
        membersJoinRequestDto.checkRequiredFieldIsNull();
        Members members = new Members();
        members.email = membersJoinRequestDto.getEmail();
        members.password = encryptPassword(membersJoinRequestDto.getPassword());
        members.picture = checkPicture(membersJoinRequestDto.getPicture());
        members.nickname = membersJoinRequestDto.getNickname();

        members.roleType = RoleType.ROLE_USER;
        members.regTime = LocalDateTime.now();
        members.oauthList = new ArrayList<>();
        members.passwordTmpYn = TFCode.FALSE;
        members.commentList = new ArrayList<>();
        members.resumeList = new ArrayList<>();
        members.taggedList = new ArrayList<>();
        return members;
    }

    public static Members createProxy(Long id) {
        Members members = new Members();
        members.id = id;
        return members;
    }

    public static Members convertByQuery(Long id, String nickname, String email, String picture) {
        Members members = new Members();
        members.id = id;
        members.nickname = nickname;
        members.email = email;
        members.picture = picture;
        return members;
    }

    private static String checkPicture(String picture) {
        if(picture != null) {
            return picture;
        }
        return RANDOM_PICTURE_URL
                +UUID.randomUUID().toString().replace("-","")
                +PICTURE_TYPE_PARAM;
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

    public String getTmpPasswordStatus() {
        // 최초 1회만 사용자에게 알림 후 변경
        // npe 체크
        String result = this.passwordTmpYn.getValue();
        tmpPasswordToFalse();
        return result;
    }

    public void checkPassword(String password) {
        if (!matchPassword(password, this.password)) { // 비밀번호가 일치하지 않을경우
            throw new ApplicationException(ErrorMessage.INVALID_MEMBER_EXCEPTION);
        }
    }

    // 비밀번호를 암호화하는 메서드
    private static String encryptPassword(String password) {
        if(password == null) {
            password = UUID.randomUUID().toString().replace("-", "");
        }
        String encryptedPassword = B_CRYPT_PASSWORD_ENCODER.encode(password);
        return encryptedPassword;
    }

    // 암호화된 비밀번호가 일치하는지 확인하는 메서드
    private boolean matchPassword(String rawPassword, String encodedPassword) {
        return B_CRYPT_PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

    // 시큐리티 설정
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(this.roleType);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof Members)){
            return false;
        }
        Members members = (Members) o;
        return Objects.equals(id,members.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void deleteTag(Tag tag) {
        this.taggedList.remove(tag);
    }
}

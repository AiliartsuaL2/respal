package com.hckst.respal.authentication.oauth.presentation.dto.response.info;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth 로그인시 받아오는 회원 정보")
public class UserInfo {
    @Schema(description = "oauth id", nullable = false)
    private String userInfoId;
    @Schema(description = "oauth 이메일", nullable = false)
    private String email;
    @Schema(description = "oauth 사진", nullable = true)
    @Column(length = 2083)
    private String image;
    @Schema(description = "oauth 닉네임", nullable = true)
    private String nickname;

    public void init() {}
    protected void setAllFiled(String userInfoId, String email, String image, String nickname) {
        this.userInfoId = userInfoId;
        this.email = email;
        this.image = image;
        this.nickname = nickname;
    }

    public UserInfo copy(UserInfo userInfo) {
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.userInfoId = userInfo.userInfoId;
        newUserInfo.email = userInfo.email;
        newUserInfo.image = userInfo.image;
        newUserInfo.nickname = userInfo.nickname;
        return newUserInfo;
    }
}

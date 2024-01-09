package com.hckst.respal.authentication.oauth.presentation.dto.response.info.kakao;

import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class KakaoUserInfo extends UserInfo{
    private String id;
    private String connectedAt;
    private KakaoPropertiesDto properties;
    private KakaoAccountDto kakaoAccount;

    @Getter
    @AllArgsConstructor
    public class KakaoPropertiesDto {
        private String nickname;
        private String profileImage;
        private String thumbnailImage;
    }

    @Getter
    @AllArgsConstructor
    public class KakaoAccountDto {
        private String email;
    }

    @Override
    public void init() {
        setAllFiled(id, kakaoAccount.email, properties.profileImage, properties.nickname);
    }
}

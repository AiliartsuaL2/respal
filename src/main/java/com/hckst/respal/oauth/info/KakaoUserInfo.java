package com.hckst.respal.oauth.info;

import com.hckst.respal.oauth.dto.KakaoAccountDto;
import com.hckst.respal.oauth.dto.KakaoPropertiesDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KakaoUserInfo extends UserInfo{
    private String connectedAt;
    private KakaoPropertiesDto properties;
    private KakaoAccountDto kakaoAccount;

}

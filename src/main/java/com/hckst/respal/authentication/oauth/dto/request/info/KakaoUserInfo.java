package com.hckst.respal.authentication.oauth.dto.request.info;

import com.hckst.respal.authentication.oauth.dto.request.KakaoAccountDto;
import com.hckst.respal.authentication.oauth.dto.request.KakaoPropertiesDto;
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

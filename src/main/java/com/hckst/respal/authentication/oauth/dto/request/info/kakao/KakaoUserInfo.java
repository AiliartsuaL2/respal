package com.hckst.respal.authentication.oauth.dto.request.info.kakao;

import com.hckst.respal.authentication.oauth.dto.request.info.kakao.KakaoAccountDto;
import com.hckst.respal.authentication.oauth.dto.request.info.kakao.KakaoPropertiesDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KakaoUserInfo {
    private String id;
    private String connectedAt;
    private KakaoPropertiesDto properties;
    private KakaoAccountDto kakaoAccount;

}

package com.hckst.respal.authentication.oauth.presentation.dto.request.info.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KakaoPropertiesDto {
    private String nickname;
    private String profileImage;
    private String thumbnailImage;
}


package com.hckst.respal.common.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialType implements BaseEnumCode<String>  {
    KAKAO("kakao"),
    GOOGLE("google"),
    GITHUB("github");
    private final String value;

}

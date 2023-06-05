package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider implements BaseEnumCode<String>  {
    KAKAO("kakao"),
    GOOGLE("google"),
    GITHUB("github"),
    COMMON("common"),
    NULL("");
    private final String value;

}

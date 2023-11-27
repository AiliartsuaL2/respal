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

    public static Provider findByValue(String value) {
        for(Provider provider : Provider.values()) {
            if(provider.value.equals(value)) {
                return provider;
            }
        }
        return NULL;
    }
}

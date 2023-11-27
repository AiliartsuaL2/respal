package com.hckst.respal.authentication.oauth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedirectType {

    CALL_BACK("callback"),
    SIGN_UP("signup"),
    NULL("");

    private final String value;

    public static RedirectType findByValue(String value) {
        for(RedirectType redirectType : RedirectType.values()) {
            if(redirectType.value.equals(value)) {
                return redirectType;
            }
        }
        return NULL;
    }
}

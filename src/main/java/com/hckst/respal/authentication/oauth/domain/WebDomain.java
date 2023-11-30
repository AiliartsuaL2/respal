package com.hckst.respal.authentication.oauth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WebDomain {

    LOCAL("localhost", "http://localhost:8080"),
    DEV("api-respal.me", "http://api-respal.me"),
    STAGING("staging", "http://respal-staging.com"),
    PRODUCT("prod", "http://respal-prod.com"),
    NULL("", "");

    private final String serverName;
    private final String domain;

    public static String findDomainByServerName(String serverName) {
        for(WebDomain redirectType : WebDomain.values()) {
            if(redirectType.serverName.equals(serverName)) {
                return redirectType.domain;
            }
        }
        return NULL.domain;
    }
}

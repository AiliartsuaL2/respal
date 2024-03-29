package com.hckst.respal.converter;

import com.hckst.respal.authentication.oauth.domain.RedirectType;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Client implements BaseEnumCode<String> {
    WEB_DEV("web-dev", "http://localhost:3000"),
    WEB_STAGING("web-staging", "http://localhost:3000"),
    WEB_PROD("web-prod", "https://respal-client.vercel.app"),
    APP("app", "app://"),
    NULL("","");

    private static final String UID_PREFIX = "?uid=";
    public static final String WEB_LOGIN_PATH = "/callback";
    public static final String WEB_SIGN_UP_PATH = "/signup/social";

    private final String environment;
    private final String redirectUrlPrefix;

    public static Client findByValue(String environment) {
        for(Client client : Client.values()) {
            if(client.environment.equals(environment)) {
                return client;
            }
        }
        return NULL;
    }

    public String getUidRedirectUrl(RedirectType redirectType, String uid) {
        if(APP.equals(this)) {
            return this.redirectUrlPrefix + redirectType.getValue() + UID_PREFIX + uid;
        }
        if(RedirectType.CALL_BACK.equals(redirectType)) {
            return this.redirectUrlPrefix + WEB_LOGIN_PATH;
        }
        if(RedirectType.SIGN_UP.equals(redirectType)) {
            return this.redirectUrlPrefix + WEB_SIGN_UP_PATH + UID_PREFIX + uid;
        }
        throw new ApplicationException(ErrorMessage.INCORRECT_OAUTH_TYPE_EXCEPTION);
    }

    @Override
    public String getValue() {
        return this.environment;
    }

    public String getCookiePath() {
        return WEB_LOGIN_PATH;
    }
}

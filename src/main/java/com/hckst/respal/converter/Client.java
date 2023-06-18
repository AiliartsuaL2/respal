package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Client implements BaseEnumCode<String> {

    WEB_DEV("web-dev"),
    WEB_STAGING("web-stg"),
    WEB_LIVE("web-live"),
    APP("app");

    private final String value;
}

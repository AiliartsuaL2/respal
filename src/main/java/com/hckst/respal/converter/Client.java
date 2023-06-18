package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Client implements BaseEnumCode<String> {

    WEB_DEV("dev"),
    WEB_STAGING("stg"),
    WEB_LIVE("live"),
    APP("app");

    private final String value;
}

package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Client implements BaseEnumCode<String> {

    WEB("web"),
    APP("app");

    private final String value;
}

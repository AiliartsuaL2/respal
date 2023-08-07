package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResumeType implements BaseEnumCode<String> {
    PUBLIC("public"),
    PRIVATE("private");

    private final String value;
}

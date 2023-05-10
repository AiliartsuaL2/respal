package com.hckst.respal.common.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TFCode implements BaseEnumCode<String> {

    TRUE("Y"),
    FALSE("N"),
    NULL("")
    ;

    private final String value;
}

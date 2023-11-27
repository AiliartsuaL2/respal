package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TFCode implements BaseEnumCode<String> {

    TRUE("Y"),
    FALSE("N"),
    NULL("");

    private final String value;

    public static TFCode findByValue(String value) {
        for(TFCode tfCode : TFCode.values()) {
            if(tfCode.value.equals(value)) {
                return tfCode;
            }
        }
        return NULL;
    }
}

package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResumeType implements BaseEnumCode<String> {
    PUBLIC("public"),
    PRIVATE("private"),
    NULL("");

    private final String value;

    public static ResumeType findByValue(String value) {
        for(ResumeType resumeType : ResumeType.values()) {
            if(resumeType.value.equals(value)) {
                return resumeType;
            }
        }
        return NULL;
    }
}

package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResumeType implements BaseEnumCode<String> {
    PUBLIC("public","hub"),
    PRIVATE("private","tag"),
    ALL("","me");

    private final String value;
    private final String type;

    public static ResumeType findByType(String type) {
        for(ResumeType resumeType : ResumeType.values()) {
            if(resumeType.type.equals(type)) {
                return resumeType;
            }
        }
        return ALL;
    }
}

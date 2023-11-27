package com.hckst.respal.converter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class ResumeTypeConverter extends AbstractBaseEnumConverter<ResumeType, String> {
    @Override
    protected ResumeType[] getValueList() {
        return ResumeType.values();
    }

    public static ResumeTypeConverter create() {
        return new ResumeTypeConverter();
    }
}

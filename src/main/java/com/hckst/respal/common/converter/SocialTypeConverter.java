package com.hckst.respal.common.converter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class SocialTypeConverter extends AbstractBaseEnumConverter<SocialType, String> {

    @Override
    protected SocialType[] getValueList() {
        return SocialType.values();
    }
}

package com.hckst.respal.common.converter;

public class RoleTypeConverter extends AbstractBaseEnumConverter<RoleType, String> {

    @Override
    protected RoleType[] getValueList() {
        return RoleType.values();
    }
}

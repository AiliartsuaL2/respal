package com.hckst.respal.converter;

public class RoleTypeConverter extends AbstractBaseEnumConverter<RoleType, String> {

    @Override
    protected RoleType[] getValueList() {
        return RoleType.values();
    }
}

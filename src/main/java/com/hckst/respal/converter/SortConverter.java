package com.hckst.respal.converter;

public class SortConverter extends AbstractBaseEnumConverter<Sort, String>{
    @Override
    protected Sort[] getValueList() {
        return Sort.values();
    }
}

package com.hckst.respal.common.converter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class ProviderConverter extends AbstractBaseEnumConverter<Provider, String> {

    @Override
    protected Provider[] getValueList() {
        return Provider.values();
    }
}

package com.hckst.respal.converter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class ProviderConverter extends AbstractBaseEnumConverter<Provider, String> {

    @Override
    protected Provider[] getValueList() {
        return Provider.values();
    }

    public static ProviderConverter create() {
        return new ProviderConverter();
    }
}

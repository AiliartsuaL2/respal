package com.hckst.respal.converter;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class ClientConverter extends AbstractBaseEnumConverter<Client, String> {

    @Override
    protected Client[] getValueList() {
        return Client.values();
    }
}

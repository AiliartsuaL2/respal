package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Client implements BaseEnumCode<String> {

    WEB("web"),
    APP("app"),
    NULL("");

    private final String value;

    public static Client findByValue(String value) {
        for(Client client : Client.values()) {
            if(client.value.equals(value)) {
                return client;
            }
        }
        return NULL;
    }
}

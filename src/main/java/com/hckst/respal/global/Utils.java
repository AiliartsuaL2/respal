package com.hckst.respal.global;

import org.springframework.util.ObjectUtils;

public class Utils {
    public static String queryParamToJson(String response) {
        if(ObjectUtils.isEmpty(response)) {
            throw new IllegalArgumentException("response 값이 존재하지 않습니다.");
        }
        if(!response.startsWith("{")) {
            return "{\"" + response.replace("&", "\",\"").replace("=", "\":\"") + "\"}";
        }
        return response;
    }
}

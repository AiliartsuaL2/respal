package com.hckst.respal.authentication.oauth.dto.response;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@AllArgsConstructor
public class RedirectResponse {
    private UserInfo userInfo;
    private String provider;

}

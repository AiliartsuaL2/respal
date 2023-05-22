package com.hckst.respal.authentication.oauth.dto.request.info;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserInfo {
    private String id;
    private String email;
    private String picture;
}

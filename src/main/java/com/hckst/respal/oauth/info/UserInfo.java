package com.hckst.respal.oauth.info;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserInfo {
    private String id;
    private String email;
    private String picture;
}

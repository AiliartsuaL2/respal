package com.hckst.respal.authentication.oauth.presentation.dto.response.info.google;

import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class GoogleUserInfo extends UserInfo{
    private String id;
    private String email;
    private String picture;
    private String name;

    public GoogleUserInfo(String id, String email, String picture, String name) {
        super(id, email, picture, name);
    }
}

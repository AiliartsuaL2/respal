package com.hckst.respal.authentication.oauth.presentation.dto.response.info.google;

import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoogleUserInfo extends UserInfo{
    private String id;
    private String picture;
    private String name;

    @Override
    public void init() {
        setAllFiled(id, super.getEmail(), picture, name);
    }
}

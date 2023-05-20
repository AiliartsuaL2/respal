package com.hckst.respal.authentication.oauth.dto.info;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoogleUserInfo extends UserInfo{
    private String name;
}

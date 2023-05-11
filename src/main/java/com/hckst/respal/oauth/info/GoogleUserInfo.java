package com.hckst.respal.oauth.info;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoogleUserInfo extends UserInfo{
    private String name;
}

package com.hckst.respal.authentication.oauth.dto.request.info.google;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GoogleUserInfo {
    private String id;
    private String email;
    private String picture;
    private String name;
}

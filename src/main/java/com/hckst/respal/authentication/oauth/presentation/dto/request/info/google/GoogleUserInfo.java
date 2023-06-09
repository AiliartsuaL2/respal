package com.hckst.respal.authentication.oauth.presentation.dto.request.info.google;

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

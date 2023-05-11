package com.hckst.respal.oauth.info;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GithubUserInfo extends UserInfo{
    private String nickname;
}

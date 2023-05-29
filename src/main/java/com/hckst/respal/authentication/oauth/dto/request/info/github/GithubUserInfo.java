package com.hckst.respal.authentication.oauth.dto.request.info.github;

import com.hckst.respal.authentication.oauth.dto.request.info.UserInfo;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GithubUserInfo {
    private String id;
    private String avatar_url;
    private String email;
    private String login;
}

package com.hckst.respal.authentication.oauth.presentation.dto.response.info.github;

import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class GithubUserInfo extends UserInfo{
    private String id;
    private String avatar_url;
    private String email;
    private String login;

    public GithubUserInfo(String id, String avatar_url, String email, String login) {
        super(id, email, avatar_url, login);
    }
}
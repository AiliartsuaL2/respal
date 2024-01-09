package com.hckst.respal.authentication.oauth.presentation.dto.response.info.github;

import com.hckst.respal.authentication.oauth.presentation.dto.response.info.UserInfo;
import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class GithubUserInfo extends UserInfo{
    private String id;
    private String avatar_url;
    private String login;

    @Override
    public void init() {
       setAllFiled(id, super.getEmail(), avatar_url, login);
    }
}

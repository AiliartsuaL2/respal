package com.hckst.respal.authentication.oauth.presentation.dto.response.info.github;

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

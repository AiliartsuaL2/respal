package com.hckst.respal.authentication.oauth.presentation.dto.request.info.github;

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

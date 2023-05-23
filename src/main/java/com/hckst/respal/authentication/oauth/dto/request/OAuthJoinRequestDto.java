package com.hckst.respal.authentication.oauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "OAuth 회원가입시 요청")
public class OAuthJoinRequestDto {
    @Schema(description = "비밀번호")
    private String password;
    @Schema(description = "닉네임")
    private String nickname;
}

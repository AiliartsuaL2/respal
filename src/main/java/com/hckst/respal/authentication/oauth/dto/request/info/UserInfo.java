package com.hckst.respal.authentication.oauth.dto.request.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "OAuth 로그인시 받아오는 회원 정보")
public class UserInfo {
    @Schema(description = "oauth id", nullable = false)
    private String id;
    @Schema(description = "oauth 이메일", nullable = false)
    private String email;
    @Schema(description = "oauth 사진", nullable = true)
    private String image;
    @Schema(description = "oauth 닉네임", nullable = true)
    private String nickname;
}

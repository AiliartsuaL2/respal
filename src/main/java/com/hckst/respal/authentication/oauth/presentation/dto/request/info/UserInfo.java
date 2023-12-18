package com.hckst.respal.authentication.oauth.presentation.dto.request.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth 로그인시 받아오는 회원 정보")
public class UserInfo {
    @Schema(description = "oauth id", nullable = false)
    private String userInfoId;
    @Schema(description = "oauth 이메일", nullable = false)
    private String email;
    @Schema(description = "oauth 사진", nullable = true)
    private String image;
    @Schema(description = "oauth 닉네임", nullable = true)
    private String nickname;
}

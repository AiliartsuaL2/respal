package com.hckst.respal.authentication.oauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "OAuth 회원가입시 요청")
public class OAuthJoinRequestDto {
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "사진")
    private String picture;
}

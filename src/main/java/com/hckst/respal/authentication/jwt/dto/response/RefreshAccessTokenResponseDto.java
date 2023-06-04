package com.hckst.respal.authentication.jwt.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "액세스 토큰 재할당시 응답")
public class RefreshAccessTokenResponseDto {
    @Schema(description = "응답 메세지")
    private String message;
    @Schema(description = "액세스 토큰")
    private String accessToken;
}

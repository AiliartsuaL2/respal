package com.hckst.respal.members.presentation.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
@Schema(description = "일반 이메일 로그인 응답")
public class MembersLoginResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String membersEmail;
}

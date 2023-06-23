package com.hckst.respal.members.presentation.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
@Schema(description = "일반 이메일 로그인 응답")
public class MembersLoginResponseDto {
    @Schema(description = "토큰 타입")
    private String grantType;
    @Schema(description = "액세스 토큰")
    private String accessToken;
    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;
    @Schema(description = "회원 이메일")
    private String membersEmail;
    @Schema(description = "임시 비밀번호 status , Y인 경우 임시 비밀번호 / N인 경우 일반 비밀번호")
    private String tmpPasswordStatus;

}

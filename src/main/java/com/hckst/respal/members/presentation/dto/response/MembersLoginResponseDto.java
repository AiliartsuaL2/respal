package com.hckst.respal.members.presentation.dto.response;


import com.hckst.respal.authentication.jwt.dto.Token;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

    public static MembersLoginResponseDto create(Token token, String tmpPasswordStatus) {
        MembersLoginResponseDto membersLoginResponseDto = new MembersLoginResponseDto();
        membersLoginResponseDto.membersEmail = token.getMembersEmail();
        membersLoginResponseDto.refreshToken = token.getRefreshToken();
        membersLoginResponseDto.accessToken = token.getAccessToken();
        membersLoginResponseDto.grantType = token.getGrantType();
        membersLoginResponseDto.tmpPasswordStatus = tmpPasswordStatus;
        return membersLoginResponseDto;
    }

    public static MembersLoginResponseDto create(Token token) {
        MembersLoginResponseDto membersLoginResponseDto = new MembersLoginResponseDto();
        membersLoginResponseDto.membersEmail = token.getMembersEmail();
        membersLoginResponseDto.refreshToken = token.getRefreshToken();
        membersLoginResponseDto.accessToken = token.getAccessToken();
        membersLoginResponseDto.grantType = token.getGrantType();
        return membersLoginResponseDto;
    }
}

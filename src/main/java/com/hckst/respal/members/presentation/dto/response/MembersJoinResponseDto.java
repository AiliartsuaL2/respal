package com.hckst.respal.members.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Schema(description = "일반 이메일 회원가입 응답")
public class MembersJoinResponseDto {
    @Schema(description = "회원가입이 정상적으로 처리되었습니다.")
    private String message = "회원가입이 정상적으로 처리되었습니다.";

    public MembersJoinResponseDto(){
    }
}

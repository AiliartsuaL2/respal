package com.hckst.respal.members.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MembersJoinResponseDto {
    private static final String message = "회원가입이 정상적으로 처리되었습니다.";

    public MembersJoinResponseDto(){
    }
}

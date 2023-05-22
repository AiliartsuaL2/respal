package com.hckst.respal.members.presentation.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class MembersLoginResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String membersEmail;
}

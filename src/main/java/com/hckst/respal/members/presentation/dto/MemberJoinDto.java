package com.hckst.respal.members.presentation.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberJoinDto {
    private String email;
    private String password;
    private String nickname;
}

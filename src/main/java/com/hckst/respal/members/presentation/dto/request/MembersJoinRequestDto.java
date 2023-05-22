package com.hckst.respal.members.presentation.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembersJoinRequestDto {
    private String email;
    private String password;
    private String nickname;
}

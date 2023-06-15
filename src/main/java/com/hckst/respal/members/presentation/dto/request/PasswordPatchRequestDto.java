package com.hckst.respal.members.presentation.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PasswordPatchRequestDto {
    private String password;
    private String uid;
}

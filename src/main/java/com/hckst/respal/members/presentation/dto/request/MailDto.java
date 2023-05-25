package com.hckst.respal.members.presentation.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class MailDto {
    private String toAddress;
    private String title;
    private String message;
    private String fromAddress;
}

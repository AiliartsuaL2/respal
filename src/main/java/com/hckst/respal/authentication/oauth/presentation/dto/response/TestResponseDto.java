package com.hckst.respal.authentication.oauth.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TestResponseDto {
    private String message;
    private Integer code;
}

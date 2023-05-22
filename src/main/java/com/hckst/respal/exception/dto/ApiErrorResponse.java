package com.hckst.respal.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class ApiErrorResponse {
    private Boolean success;
    private Integer code;
    private String message;
}

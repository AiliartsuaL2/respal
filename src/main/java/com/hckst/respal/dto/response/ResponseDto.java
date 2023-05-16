package com.hckst.respal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
@Builder
public class ResponseDto {
    private Boolean success;
    private Integer code;
    private Object data;
}

package com.hckst.respal.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Schema(description = "에러 응답")
@AllArgsConstructor
public class ApiErrorResponse {

    @Schema(description = "Http 응답 코드")
    private Integer statusCode;

    @Schema(description = "에러 메세지")
    private String message;

    @Schema(description = "커스텀 에러코드")
    private String errorCode;

    private ApiErrorResponse() {
    }
}

package com.hckst.respal.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "응답")
public class ApiCommonResponse<T> {
    @Schema(description = "Http 응답 코드")
    private final Integer statusCode;

    @Schema(description = "응답 데이터")
    private final T result;

    public ApiCommonResponse(Integer statusCode, T result) {
        this.statusCode = statusCode;
        this.result = result;
    }
}

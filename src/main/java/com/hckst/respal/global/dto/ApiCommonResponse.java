package com.hckst.respal.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "응답")
@AllArgsConstructor
@NoArgsConstructor
public class ApiCommonResponse<T> {
    @Schema(description = "Http 응답 코드")
    private Integer statusCode;

    @Schema(description = "응답 데이터")
    private T result;
}

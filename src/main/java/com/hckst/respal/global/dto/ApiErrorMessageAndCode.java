package com.hckst.respal.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "에러 메세지, 에러 코드")
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorMessageAndCode {
    @Schema(description = "에러 메세지")
    private String message;

    @Schema(description = "커스텀 에러코드")
    private String errorCode;
}

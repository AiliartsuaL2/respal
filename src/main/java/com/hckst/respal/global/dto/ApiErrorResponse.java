package com.hckst.respal.global.dto;

import com.hckst.respal.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@Schema(description = "에러 응답")
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {

    @Schema(description = "Http 응답 코드")
    private Integer statusCode;

    private ApiErrorMessageAndCode result;

    public ApiErrorResponse(ErrorMessage errorMessage) {
        this.statusCode = errorMessage.getHttpStatus().value();
        this.result = new ApiErrorMessageAndCode(errorMessage.getMsg(), errorMessage.getErrorCode());
    }
}

package com.hckst.respal.exception.oauth.dto;

import com.hckst.respal.global.dto.ApiErrorMessageAndCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "에러 메세지, 에러 코드")
public class OAuthLoginErrorResponse extends ApiErrorMessageAndCode{
    @Schema(description = "사용자 정보가 담긴 OAuth_tmp의 uid")
    private String uid;

    public OAuthLoginErrorResponse(String message, String errorCode, String uid) {
        super(message, errorCode);
        this.uid = uid;
    }
}

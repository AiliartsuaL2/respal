package com.hckst.respal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OAuthLoginException extends RuntimeException {
    private final String errorCode;
    private final Integer statusCode;
    private final String uid;
    private final HttpStatus httpStatus;

    public OAuthLoginException(ErrorMessage errorMessage,String uid) {
        super(errorMessage.getMsg());
        this.errorCode = errorMessage.getErrorCode();
        this.statusCode = errorMessage.getCode();
        this.uid = uid;
        this.httpStatus = errorMessage.getHttpStatus();
    }
}

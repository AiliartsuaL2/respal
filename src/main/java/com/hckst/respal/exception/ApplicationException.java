package com.hckst.respal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException{

    private final String errorCode;
    private final Integer statusCode;
    private final HttpStatus httpStatus;

    public ApplicationException(ErrorMessage errorMessage) {
        super(errorMessage.getMsg());
        this.errorCode = errorMessage.getErrorCode();
        this.statusCode = errorMessage.getCode();
        this.httpStatus = errorMessage.getHttpStatus();
    }
}

package com.hckst.respal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException{

    private final String errorCode;
    private final Integer statusCode;
    private final HttpStatus httpStatus;

    protected ApplicationException(int statusCode , HttpStatus httpStatus, String errorCode , String message) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
        this.httpStatus = httpStatus;
    }
}

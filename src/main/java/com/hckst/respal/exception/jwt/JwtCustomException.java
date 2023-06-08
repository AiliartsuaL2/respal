package com.hckst.respal.exception.jwt;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class JwtCustomException extends ApplicationException {
    protected JwtCustomException(int statusCode, HttpStatus httpStatus,String errorCode, String message) {
        super(statusCode, httpStatus, errorCode, message);
    }
}

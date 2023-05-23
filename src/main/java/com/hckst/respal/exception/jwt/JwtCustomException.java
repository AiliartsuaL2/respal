package com.hckst.respal.exception.jwt;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class JwtCustomException extends ApplicationException {
    protected JwtCustomException(int errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}

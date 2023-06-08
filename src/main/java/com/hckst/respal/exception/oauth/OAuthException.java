package com.hckst.respal.exception.oauth;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class OAuthException extends ApplicationException {
    protected OAuthException(int statusCode, HttpStatus httpStatus,String errorCode, String message) {
        super(statusCode, httpStatus, errorCode, message);
    }
}

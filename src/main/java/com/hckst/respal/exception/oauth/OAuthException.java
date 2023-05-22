package com.hckst.respal.exception.oauth;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class OAuthException extends ApplicationException {
    protected OAuthException(int errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}

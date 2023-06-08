package com.hckst.respal.exception.oauth;

import org.springframework.http.HttpStatus;

public class NoSuchOAuthCodeException extends OAuthException{

    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "OAuth Code값이 존재하지 않습니다.";
    private static final String ERROR_CODE = "R101";

    public NoSuchOAuthCodeException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

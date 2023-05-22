package com.hckst.respal.exception.oauth;

import org.springframework.http.HttpStatus;

public class NoSuchOAuthCodeException extends OAuthException{

    private static final int CODE = 400;
    private static final String MESSAGE = "OAuthCode값이 존재하지 않습니다.";

    public NoSuchOAuthCodeException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

package com.hckst.respal.exception.oauth;

import org.springframework.http.HttpStatus;

public class NoSuchOAuthTmpEndpointException extends OAuthException{

    private static final int CODE = 400;
    private static final String MESSAGE = "해당 Endpoint에 해당하는 값이 존재하지 않습니다.";

    public NoSuchOAuthTmpEndpointException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

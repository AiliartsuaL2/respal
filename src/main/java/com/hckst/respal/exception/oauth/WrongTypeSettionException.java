package com.hckst.respal.exception.oauth;

import org.springframework.http.HttpStatus;

public class WrongTypeSettionException extends OAuthException{

    private static final int CODE = 400;
    private static final String MESSAGE = "type 설정이 잘못되었습니다.";

    public WrongTypeSettionException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

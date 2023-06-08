package com.hckst.respal.exception.oauth;

import org.springframework.http.HttpStatus;

public class WrongTypeSettionException extends OAuthException{

    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "type 설정이 잘못되었습니다.";
    private static final String ERROR_CODE = "R103";

    public WrongTypeSettionException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

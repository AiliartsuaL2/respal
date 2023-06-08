package com.hckst.respal.exception.oauth;

import org.springframework.http.HttpStatus;

public class NoSuchOAuthTmpUidException extends OAuthException{

    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "해당 UID에 해당하는 값이 존재하지 않습니다.";
    private static final String ERROR_CODE = "R102";

    public NoSuchOAuthTmpUidException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

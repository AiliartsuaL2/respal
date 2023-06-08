package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class MalformedTokenException extends JwtCustomException{
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "지원하지 않는 토큰 방식입니다.";
    private static final String ERROR_CODE = "R003";

    public MalformedTokenException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }

}

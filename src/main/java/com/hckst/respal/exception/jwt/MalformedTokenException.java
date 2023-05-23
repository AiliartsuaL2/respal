package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class MalformedTokenException extends JwtCustomException{
    private static final int CODE = 400;
    private static final String MESSAGE = "지원하지 않는 토큰 방식입니다.";

    public MalformedTokenException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

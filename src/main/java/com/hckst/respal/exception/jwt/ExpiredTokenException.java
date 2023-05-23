package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends JwtCustomException {
    private static final int CODE = 400;
    private static final String MESSAGE = "만료된 토큰 정보입니다.";

    public ExpiredTokenException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

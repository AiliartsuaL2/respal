package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class IncorrectRefreshTokenException extends JwtCustomException {
    private static final int CODE = 400;
    private static final String MESSAGE = "일치하지 않는 refresh token 입니다.";

    public IncorrectRefreshTokenException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

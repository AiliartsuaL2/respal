package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class NotExistRefreshTokenException extends JwtCustomException{
    private static final int STATUS_CODE = 401;
    private static final String MESSAGE = "refresh token이 존재하지 않습니다.";
    private static final String ERROR_CODE = "R004";

    public NotExistRefreshTokenException() {
        super(STATUS_CODE, HttpStatus.UNAUTHORIZED, ERROR_CODE, MESSAGE);
    }
}

package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class IncorrectRefreshTokenException extends JwtCustomException {
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "일치하지 않는 refresh token 입니다.";
    private static final String ERROR_CODE = "R002";

    public IncorrectRefreshTokenException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

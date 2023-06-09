package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends JwtCustomException {
    private static final int STATUS_CODE = 401;
    private static final String MESSAGE = "만료된 토큰 정보입니다.";
    private static final String ERROR_CODE = "R001";

    public ExpiredTokenException() {
        super(STATUS_CODE, HttpStatus.UNAUTHORIZED, ERROR_CODE, MESSAGE);
    }
}

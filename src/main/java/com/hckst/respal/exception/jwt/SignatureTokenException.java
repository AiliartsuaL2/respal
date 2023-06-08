package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class SignatureTokenException extends JwtCustomException{
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "잘못된 토큰 정보입니다.";
    private static final String ERROR_CODE = "R006";

    public SignatureTokenException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

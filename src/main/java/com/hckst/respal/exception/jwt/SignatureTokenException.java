package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class SignatureTokenException extends JwtCustomException{
    private static final int CODE = 400;
    private static final String MESSAGE = "잘못된 토큰 정보입니다.";

    public SignatureTokenException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

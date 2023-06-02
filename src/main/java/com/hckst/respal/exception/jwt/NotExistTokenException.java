package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class NotExistTokenException extends JwtCustomException{
    private static final int CODE = 401;
    private static final String MESSAGE = "토큰 정보가 존재하지 않습니다.";

    public NotExistTokenException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

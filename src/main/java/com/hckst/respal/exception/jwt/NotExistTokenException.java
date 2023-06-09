package com.hckst.respal.exception.jwt;

import org.springframework.http.HttpStatus;

public class NotExistTokenException extends JwtCustomException{
    private static final int STATUS_CODE = 401;
    private static final String MESSAGE = "토큰 정보가 존재하지 않습니다.";
    private static final String ERROR_CODE = "R005";

    public NotExistTokenException() {
        super(STATUS_CODE, HttpStatus.UNAUTHORIZED, ERROR_CODE, MESSAGE);
    }
}

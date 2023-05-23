package com.hckst.respal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    NOT_EXIST_MEMBER(HttpStatus.BAD_REQUEST, "사용자가 존재하지 않습니다.");

    private final String msg;
    private final HttpStatus httpStatus;
    private final int code;

    ErrorMessage(HttpStatus httpStatus, String msg){
        this.msg = msg;
        this. httpStatus = httpStatus;
        this.code = httpStatus.value();
    }
}
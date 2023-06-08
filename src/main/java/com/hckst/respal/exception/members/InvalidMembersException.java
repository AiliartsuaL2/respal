package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class InvalidMembersException extends MembersException{
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "유효하지 않은 유저입니다.";
    private static final String ERROR_CODE = "R202";

    public InvalidMembersException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

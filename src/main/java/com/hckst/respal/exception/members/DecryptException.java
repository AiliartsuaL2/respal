package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class DecryptException extends MembersException{
    private static final int STATUS_CODE = 500;
    private static final String MESSAGE = "복호화에 실패하였습니다.";
    private static final String ERROR_CODE = "R205";

    public DecryptException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

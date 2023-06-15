package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class EncryptException extends MembersException {
    private static final int STATUS_CODE = 500;
    private static final String MESSAGE = "암호화에 실패하였습니다.";
    private static final String ERROR_CODE = "R204";

    public EncryptException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

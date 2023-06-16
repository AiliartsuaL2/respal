package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class NotExistMembersException extends MembersException {
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "해당 회원이 존재하지 않습니다.";
    private static final String ERROR_CODE = "R205";

    public NotExistMembersException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

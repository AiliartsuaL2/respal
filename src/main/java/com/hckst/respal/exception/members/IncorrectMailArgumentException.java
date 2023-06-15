package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class IncorrectMailArgumentException extends MembersException{

    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "메일 발송 조건이 충족되지 않았습니다.";
    private static final String ERROR_CODE = "R203";

    public IncorrectMailArgumentException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

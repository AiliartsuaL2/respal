package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class ChangePasswordException extends MembersException {
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "임시 비밀번호를 변경해야 서비스 이용을 하실 수 있습니다.";
    private static final String ERROR_CODE = "R204";

    public ChangePasswordException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

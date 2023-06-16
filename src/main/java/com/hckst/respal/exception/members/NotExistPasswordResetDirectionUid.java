package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class NotExistPasswordResetDirectionUid extends MembersException {

    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "해당 UID와 일치하는 회원정보가 없습니다.";
    private static final String ERROR_CODE = "R204";

    public NotExistPasswordResetDirectionUid() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

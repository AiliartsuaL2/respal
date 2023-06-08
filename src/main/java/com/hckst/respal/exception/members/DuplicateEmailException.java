package com.hckst.respal.exception.members;

import com.hckst.respal.exception.oauth.OAuthException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends MembersException {
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "이미 존재하는 이메일 입니다.";
    private static final String ERROR_CODE = "R201";

    public DuplicateEmailException() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

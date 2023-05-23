package com.hckst.respal.exception.members;

import com.hckst.respal.exception.oauth.OAuthException;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends MembersException {

    private static final int CODE = 400;
    private static final String MESSAGE = "이미 존재하는 이메일 입니다.";

    public DuplicateEmailException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

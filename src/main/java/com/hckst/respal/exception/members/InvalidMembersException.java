package com.hckst.respal.exception.members;

import org.springframework.http.HttpStatus;

public class InvalidMembersException extends MembersException{

        private static final int CODE = 400;
        private static final String MESSAGE = "유효하지 않은 유저입니다.";

        public InvalidMembersException() {
            super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
        }
}

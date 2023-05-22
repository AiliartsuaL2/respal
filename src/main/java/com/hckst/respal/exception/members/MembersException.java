package com.hckst.respal.exception.members;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class MembersException extends ApplicationException {
    protected MembersException(int errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}

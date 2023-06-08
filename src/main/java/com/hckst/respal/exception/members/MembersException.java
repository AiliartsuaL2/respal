package com.hckst.respal.exception.members;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class MembersException extends ApplicationException {
    protected MembersException(int statusCode, HttpStatus httpStatus,String errorCode, String message) {
        super(statusCode, httpStatus, errorCode, message);
    }
}

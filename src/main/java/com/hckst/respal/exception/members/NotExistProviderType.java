package com.hckst.respal.exception.members;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotExistProviderType extends ApplicationException {
    private static final int CODE = 400;
    private static final String MESSAGE = "Provider 타입이 존재하지 않습니다.";

    public NotExistProviderType() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}

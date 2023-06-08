package com.hckst.respal.exception.members;

import com.hckst.respal.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NotExistProviderType extends ApplicationException {
    private static final int STATUS_CODE = 400;
    private static final String MESSAGE = "Provider 타입이 존재하지 않습니다.";
    private static final String ERROR_CODE = "R203";

    public NotExistProviderType() {
        super(STATUS_CODE, HttpStatus.BAD_REQUEST, ERROR_CODE, MESSAGE);
    }
}

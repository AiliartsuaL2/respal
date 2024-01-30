package com.hckst.respal.exception.oauth;

import com.hckst.respal.exception.ErrorMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.net.URI;

@Getter
public class OAuthLoginException extends RuntimeException {
    private final String errorCode;
    private final Integer statusCode;
    private final String uid;
    private final HttpStatus httpStatus;
    private final URI redirectUrl;

    public OAuthLoginException(ErrorMessage errorMessage, String uid, URI redirectUrl) {
        super(errorMessage.getMsg());
        this.errorCode = errorMessage.getErrorCode();
        this.statusCode = errorMessage.getCode();
        this.uid = uid;
        this.httpStatus = errorMessage.getHttpStatus();
        this.redirectUrl = redirectUrl;
    }
}

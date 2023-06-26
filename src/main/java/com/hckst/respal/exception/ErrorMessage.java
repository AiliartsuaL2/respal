package com.hckst.respal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    // 0번대 JWT 관련 오류
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"만료된 토큰 정보입니다.","R001"),
    INCORRECT_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"일치하지 않는 REFRESH TOKEN 입니다.","R002"),
    MALFORMED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"지원하지 않는 토큰 방식입니다.","R003"),
    NOT_EXIST_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"REFRESH TOKEN이 존재하지 않습니다.","R004"),
    NOT_EXIST_TOKEN_INFO_EXCEPTION(HttpStatus.UNAUTHORIZED,"토큰 정보가 존재하지 않습니다.","R005"),
    SIGNATURE_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"잘못된 토큰 정보입니다.","R006"),

    // 100번대 OAuth 관련 오류
    NO_SUCH_OAUTH_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"OAuth Code값이 존재하지 않습니다.","R101"),
    NO_SUCH_OAUTH_TMP_UID_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 UID에 해당하는 값이 존재하지 않습니다.","R102"),
    INCORRECT_OAUTH_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST,"OAuth type 설정이 잘못되었습니다.","R103"),

    // 201번대 Members 관련 오류
    DUPLICATE_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일 입니다.","R201"),
    INVALID_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST,"유효하지 않은 유저입니다.","R201"),
    INCORRECT_MAIL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST,"메일 발송 조건이 충족되지 않았습니다.","R201"),
    NOT_EXIST_PROVIDER_TYPE(HttpStatus.BAD_REQUEST,"Provider 타입이 존재하지 않습니다.","R201"),
    NOT_EXIST_MEMBER_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 회원이 존재하지 않습니다.","R201");


    private final String msg;
    private final HttpStatus httpStatus;
    private final int code;
    private final String errorCode;
    ErrorMessage(HttpStatus httpStatus, String msg, String errorCode){
        this.msg = msg;
        this. httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.errorCode = errorCode;
    }
}
package com.hckst.respal.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    INCORRECT_MEMBER_INFO(HttpStatus.BAD_REQUEST, "회원 정보가 일치하지 않습니다."),
    NOT_EXIST_MEMBER(HttpStatus.BAD_REQUEST, "사용자가 존재하지 않습니다."),
    NOT_EXIST_BOARD(HttpStatus.BAD_REQUEST, "게시판이 존재하지 않습니다."),
    UNAUTHORIZED_PERMISSION(HttpStatus.FORBIDDEN, "해당 작업에 대해 권한이 없습니다."),
    DUPLICATE_MEMBER_EMAIL(HttpStatus.BAD_REQUEST,"중복된 사용자 이메일 입니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "중복된 카테고리 이름 입니다."),
    NOT_REGISTED_BUSINESS_REG_NUM(HttpStatus.BAD_REQUEST, "진위 확인이 되지 않는 사업자 정보 입니다."),
    DUPLICATE_BUSINESS_REG_NUM(HttpStatus.BAD_REQUEST,"중복된 사업자 등록번호 입니다."),
    INCORRECT_ARGUMENTS_TO_OPENAPI(HttpStatus.BAD_REQUEST, "진위 확인을 위한 인자가 잘못되었습니다."),
    INCORRECT_CATEGORYNAME(HttpStatus.BAD_REQUEST, "해당 카테고리 이름이 존재하지 않습니다."),
    NOT_EXIST_COMMENT(HttpStatus.BAD_REQUEST, "해당 댓글이 존재하지 않습니다."),
    NOT_EXIST_BUSINESS_CATEGORY_NAME(HttpStatus.BAD_REQUEST,"존재하지 않는 업종명 입니다."),
    NOT_EXIST_MESSAGE(HttpStatus.BAD_REQUEST, "해당 메세지가 존재하지 않습니다."),

    UNKNOWN_ERROR(HttpStatus.UNAUTHORIZED,"인증 토큰이 존재하지 않습니다."),
    WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED,"잘못된 토큰 정보입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 토큰 정보입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED,"지원하지 않는 토큰 방식입니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED,"알 수 없는 이유로 요청이 거절되었습니다."),


    DO_NOT_BLOCKED_SELF(HttpStatus.BAD_REQUEST,"자기 자신은 차단 할 수 없습니다."),
    NOT_BLOCKED_MEMBERS(HttpStatus.BAD_REQUEST,"차단한 사용자가 아닙니다."),
    ALREADY_BLOCKED_MEMBERS(HttpStatus.BAD_REQUEST,"이미 차단한 사용자 입니다."),
    NOT_EXIST_BLACKLIST(HttpStatus.BAD_REQUEST,"차단한 사용자가 없습니다."),
    REJECTED_MESSAGE_BY_BLOCK(HttpStatus.BAD_REQUEST,"차단한 사용자에게는 메세지를 전송하실 수 없습니다."),
    BEFORE_USED_PASSWORD (HttpStatus.BAD_REQUEST, "기존에 사용하던 비밀번호입니다."),
    NOT_EXIST_REFRESHTOKEN(HttpStatus.BAD_REQUEST, "RefreshToken이 존재하지 않습니다.") ;

    private final String msg;
    private final HttpStatus httpStatus;
    private final int code;

    ErrorMessage(HttpStatus httpStatus, String msg){
        this.msg = msg;
        this. httpStatus = httpStatus;
        this.code = httpStatus.value();
    }
}
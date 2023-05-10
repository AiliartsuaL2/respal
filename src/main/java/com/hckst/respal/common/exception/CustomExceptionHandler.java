package com.hckst.respal.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@RestControllerAdvice("com.hckst.respal.controller") // exception 스코프를 패키지 레벨로
public class CustomExceptionHandler {
    @ExceptionHandler(RejectedExecutionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String RejectedExecutionException(RejectedExecutionException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        return message;
    }


    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String NoSuchElementException(NoSuchElementException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        return message;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String IllegalArgumentException(IllegalArgumentException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message =  ex.getMessage();
        log.error(message,stackTraceElements[0]);
        return message;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String UsernameNotFoundException(UsernameNotFoundException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message =  ex.getMessage();
        log.error(message,stackTraceElements[0]);
        return message;
    }


}
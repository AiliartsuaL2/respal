package com.hckst.respal.common.exception;

import com.hckst.respal.dto.response.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ResponseDto> RejectedExecutionException(RejectedExecutionException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ResponseDto response = ResponseDto.builder()
                .success(false)
                .code(400)
                .data(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDto> NoSuchElementException(NoSuchElementException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ResponseDto response = ResponseDto.builder()
                .success(false)
                .code(400)
                .data(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> IllegalArgumentException(IllegalArgumentException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message =  ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ResponseDto response = ResponseDto.builder()
                .success(false)
                .code(400)
                .data(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseDto> UsernameNotFoundException(UsernameNotFoundException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message =  ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ResponseDto response = ResponseDto.builder()
                .success(false)
                .code(400)
                .data(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}
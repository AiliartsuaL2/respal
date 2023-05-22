package com.hckst.respal.exception;

import com.hckst.respal.exception.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@RestControllerAdvice // exception 스코프를 패키지 레벨로
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorResponse> applicationException(ApplicationException ex){
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RejectedExecutionException.class)
    public ResponseEntity<ApiErrorResponse> RejectedExecutionException(RejectedExecutionException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> methodArgumentNotValidException(RejectedExecutionException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> NoSuchElementException(NoSuchElementException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message = ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> IllegalArgumentException(IllegalArgumentException ex) {
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String message =  ex.getMessage();
        log.error(message,stackTraceElements[0]);
        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(400)
                .message(message)
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}
package com.hckst.respal.authentication.jwt.handler;

import com.hckst.respal.exception.dto.ApiErrorResponse;
import com.hckst.respal.exception.jwt.JwtCustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (JwtCustomException ex) {
            setResponse(ex);
        }
    }

    private ResponseEntity<ApiErrorResponse> setResponse(JwtCustomException ex) throws RuntimeException, IOException {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(new ApiErrorResponse(ex.getErrorCode(),ex.getMessage()));
    }
}

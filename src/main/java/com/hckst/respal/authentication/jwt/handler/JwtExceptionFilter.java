package com.hckst.respal.authentication.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import com.hckst.respal.global.dto.ApiErrorMessageAndCode;
import com.hckst.respal.global.dto.ApiErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
    private final JwtErrorResponseHandler jwtErrorResponseHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (SignatureException e) {
            jwtErrorResponseHandler.generateJwtErrorResponse(response, ErrorMessage.SIGNATURE_TOKEN_EXCEPTION);
        } catch (MalformedJwtException e) {
            jwtErrorResponseHandler.generateJwtErrorResponse(response, ErrorMessage.MALFORMED_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            jwtErrorResponseHandler.generateJwtErrorResponse(response, ErrorMessage.EXPIRED_TOKEN_EXCEPTION);
        } catch (IllegalArgumentException e) {
            jwtErrorResponseHandler.generateJwtErrorResponse(response, ErrorMessage.INCORRECT_REFRESH_TOKEN_EXCEPTION);
        }
    }
}
package com.hckst.respal.authentication.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hckst.respal.global.dto.ApiErrorResponse;
import com.hckst.respal.exception.jwt.JwtCustomException;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (JwtCustomException ex) {
            setResponse(ex, response);
        }
    }

    private void setResponse(JwtCustomException ex, HttpServletResponse response) throws RuntimeException, IOException {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(ex.getStatusCode(), ex.getMessage(), ex.getErrorCode());
        response.setStatus(ex.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

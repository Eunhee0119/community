package com.example.community.auth.exception;

import com.example.community.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    protected ApiResponse<?> handleUnauthorizedException(InvalidTokenException e) {
        return ApiResponse.of(HttpStatus.UNAUTHORIZED,e.getMessage(),null);
    }
}

package com.example.community.board.exception;

import com.example.community.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(UnsupportedImageFormatException.class)
    protected ApiResponse<?> handleUnsupportedImageFormatException(UnsupportedImageFormatException e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST,e.getMessage(),null);
    }

    @ExceptionHandler(NoSuchBoardException.class)
    protected ApiResponse<?> handleNoSuchBoardException(NoSuchBoardException e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST,e.getMessage(),null);
    }
}

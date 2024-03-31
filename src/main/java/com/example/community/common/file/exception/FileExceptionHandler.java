package com.example.community.common.file.exception;

import com.example.community.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FileExceptionHandler {

    @ExceptionHandler(FileUploadFailureException.class)
    protected ApiResponse<?> handleFileUploadFailureException(FileUploadFailureException e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST,e.getMessage(),null);
    }

    @ExceptionHandler(FileEmptyException.class)
    protected ApiResponse<?> handleFileEmptyException(FileEmptyException e) {
        return ApiResponse.of(HttpStatus.NOT_FOUND,e.getMessage(),null);
    }
}

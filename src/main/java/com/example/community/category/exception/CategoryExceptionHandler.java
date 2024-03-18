package com.example.community.category.exception;

import com.example.community.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CategoryExceptionHandler {

    @ExceptionHandler(NoSuchCategoryFoundException.class)
    protected ApiResponse<?> handleNoSuchCategoryFoundException(NoSuchCategoryFoundException e) {
        return ApiResponse.of(HttpStatus.NOT_FOUND,e.getMessage(),null);
    }

    @ExceptionHandler(BadRequestCategoryException.class)
    protected ApiResponse<?> handleBadRequestCategoryException(BadRequestCategoryException e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST,e.getMessage(),null);
    }
}

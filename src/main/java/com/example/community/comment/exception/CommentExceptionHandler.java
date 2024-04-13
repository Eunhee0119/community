package com.example.community.comment.exception;

import com.example.community.ApiResponse;
import com.example.community.board.exception.NoSuchBoardException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler(NoSuchCommentException.class)
    protected ApiResponse<?> handleNoSuchCommentException(NoSuchCommentException e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST,e.getMessage(),null);
    }

    @ExceptionHandler(BadRequestCommentException.class)
    protected ApiResponse<?> handleBadRequestCommentException(BadRequestCommentException e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST,e.getMessage(),null);
    }
}

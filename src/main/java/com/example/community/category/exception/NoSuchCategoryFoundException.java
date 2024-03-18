package com.example.community.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.OK)
public class NoSuchCategoryFoundException extends RuntimeException{
    public NoSuchCategoryFoundException(String message) {
        super(message);
    }

}

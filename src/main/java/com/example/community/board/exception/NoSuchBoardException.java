package com.example.community.board.exception;

public class NoSuchBoardException extends RuntimeException{
    public NoSuchBoardException(String message) {
        super(message);
    }
}

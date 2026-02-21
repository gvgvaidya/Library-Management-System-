package com.library.exception;

public class InvalidISBNException extends RuntimeException {
    public InvalidISBNException(String message) {
        super(message);
    }
}


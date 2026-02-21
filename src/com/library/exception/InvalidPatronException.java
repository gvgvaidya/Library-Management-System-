package com.library.exception;

public class InvalidPatronException extends RuntimeException {
    public InvalidPatronException(String message) {
        super(message);
    }
}


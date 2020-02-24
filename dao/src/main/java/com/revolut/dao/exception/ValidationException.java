package com.revolut.dao.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}

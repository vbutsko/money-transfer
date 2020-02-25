package com.revolut.dao.exception;

public class DaoValidationException extends RuntimeException {

    public DaoValidationException(String message) {
        super(message);
    }

}

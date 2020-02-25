package com.revolut.dao.exception;

import java.io.IOException;

public class DaoValidationException extends IOException {

    public DaoValidationException(String message) {
        super(message);
    }

}

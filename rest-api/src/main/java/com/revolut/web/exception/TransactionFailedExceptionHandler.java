package com.revolut.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.revolut.core.exception.TransactionFailedException;

@Provider
public class TransactionFailedExceptionHandler implements ExceptionMapper<TransactionFailedException> {

    @Override
    public Response toResponse(TransactionFailedException e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e.getMessage())
                .build();
    }

}

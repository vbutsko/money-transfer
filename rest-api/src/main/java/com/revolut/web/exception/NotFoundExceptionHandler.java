package com.revolut.web.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.revolut.core.exception.NotFoundException;

@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException>
{
    @Override
    public Response toResponse(NotFoundException exception)
    {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}

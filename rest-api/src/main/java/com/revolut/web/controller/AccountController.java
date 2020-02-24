package com.revolut.web.controller;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.revolut.core.dto.AccountDto;
import com.revolut.core.service.AccountService;

import static javax.ws.rs.core.Response.Status.OK;

@Path("/accounts")
public class AccountController {

    private final Gson json = new Gson();

    private AccountService accountService = AccountService.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        List<AccountDto> accounts = accountService.getAll();
        return Response.status(OK)
                .entity(json.toJson(accounts))
                .build();
    }

}

package com.revolut.web;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.revolut.dto.AccountDto;
import com.revolut.service.AccountService;

@Path("/accounts")
public class AccountController {

    private final Gson json = new Gson();

    private AccountService accountService = AccountService.getInstance();

    @GET
    @Produces("application/json")
    public Response get() {
        Optional<AccountDto> accountDto = accountService.getById("account-1");
        if (accountDto.isPresent()) {
            return Response.status(Response.Status.OK).entity(json.toJson(accountDto)).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}

package com.revolut.web.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.revolut.core.dto.TransactionDto;
import com.revolut.core.dto.TransferTransactionCreateDto;
import com.revolut.core.strategy.TransactionStrategy;
import com.revolut.web.request.TransferTransactionCreateRequest;

@Path("/accounts/{accountId}/transactions")
public class AccountTransactionController {

    private final Gson json = new Gson();

    private TransactionStrategy transactionStrategy = TransactionStrategy.getInstance();

    @POST
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(@PathParam("accountId") String accountId, String body) {
        TransferTransactionCreateRequest createRequest = json.fromJson(body, TransferTransactionCreateRequest.class);
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setAccountId(accountId);
        createDto.setAmount(createRequest.getAmount());
        createDto.setDestinationAccountId(createRequest.getDestinationAccountId());
        TransactionDto transactionDto = transactionStrategy.createTransaction(createDto);
        return Response.status(Response.Status.OK)
                .entity(json.toJson(transactionDto))
                .build();
    }

}

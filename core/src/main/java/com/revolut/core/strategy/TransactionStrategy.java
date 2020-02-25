package com.revolut.core.strategy;

import com.revolut.core.dto.TransactionCreateDto;
import com.revolut.core.dto.TransactionDto;
import com.revolut.core.dto.TransferTransactionCreateDto;
import com.revolut.core.exception.TransactionFailedException;
import com.revolut.core.service.impl.TransferTransactionService;

public class TransactionStrategy {

    private static TransactionStrategy instance;

    private TransactionStrategy() {
    }

    public synchronized static TransactionStrategy getInstance() {
        if (instance == null) {
            instance = new TransactionStrategy();
        }
        return instance;
    }

    public TransactionDto createTransaction(TransactionCreateDto createDto) {
        if (createDto instanceof TransferTransactionCreateDto) {
            return TransferTransactionService.getInstance().createTransaction(createDto);
        } else {
            throw new TransactionFailedException("Transaction is not supported now");
        }
    }

}

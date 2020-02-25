package com.revolut.core.service;

import com.revolut.core.dto.TransactionCreateDto;
import com.revolut.core.dto.TransactionDto;

public interface TransactionService<T extends TransactionCreateDto, R extends TransactionDto> {

    R createTransaction(T createDto);

}

package com.revolut.converter;

import com.revolut.dto.AccountDto;
import com.revolut.model.Account;

public class AccountEntityToDtoConverter implements Converter<Account, AccountDto> {

    private TransactionEntityToDtoConverter transactionConverter;

    public AccountEntityToDtoConverter() {
        transactionConverter = new TransactionEntityToDtoConverter();
    }

    @Override
    public AccountDto convert(Account input) {
        return AccountDto.builder()
                .id(input.getUuid())
                .currency(input.getCurrency())
                .total(input.getTotal())
                .transactionHistory(transactionConverter.convertCollection(input.getTransactionHistory()))
                .build();
    }

}

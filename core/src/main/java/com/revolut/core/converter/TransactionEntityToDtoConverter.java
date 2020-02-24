package com.revolut.core.converter;

import com.revolut.dao.model.Transaction;
import com.revolut.core.dto.TransactionDto;

public class TransactionEntityToDtoConverter implements Converter<Transaction, TransactionDto> {

    @Override
    public TransactionDto convert(Transaction input) {
        return TransactionDto.builder()
                .id(input.getUuid())
                .amount(input.getAmount())
                .createdAt(input.getCreatedAt())
                .currency(input.getCurrency())
                .description(input.getDescription())
                .type(input.getType())
                .build();
    }

}

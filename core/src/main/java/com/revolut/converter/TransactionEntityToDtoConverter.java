package com.revolut.converter;

import com.revolut.dto.TransactionDto;
import com.revolut.model.Transaction;

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

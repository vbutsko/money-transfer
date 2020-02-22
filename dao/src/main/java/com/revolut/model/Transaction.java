package com.revolut.model;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction extends DomainEntity {

    private String uuid;
    private TransactionType type;
    private String from;
    private BigDecimal amount;
    private Currency currency;

}

package com.revolut.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Collections.emptyList;

@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account extends DomainEntity {

    private String uuid;
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;
    @Builder.Default
    private Currency currency = Currency.USD;
    @Builder.Default
    private List<Transaction> transactionHistory = emptyList();

}

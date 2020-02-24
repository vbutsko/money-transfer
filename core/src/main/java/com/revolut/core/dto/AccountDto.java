package com.revolut.core.dto;

import java.math.BigDecimal;
import java.util.List;

import com.revolut.dao.model.Currency;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.util.Collections.emptyList;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class AccountDto {

    private String id;
    private BigDecimal total;
    private Currency currency;
    @Builder.Default
    private List<TransactionDto> transactionHistory = emptyList();

}

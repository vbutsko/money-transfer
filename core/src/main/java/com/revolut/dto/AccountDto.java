package com.revolut.dto;

import java.math.BigDecimal;
import java.util.List;

import com.revolut.model.Currency;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class AccountDto {

    private String id;
    @Builder.Default
    private BigDecimal total;
    @Builder.Default
    private Currency currency;
    @Builder.Default
    private List<TransactionDto> transactionHistory;
}

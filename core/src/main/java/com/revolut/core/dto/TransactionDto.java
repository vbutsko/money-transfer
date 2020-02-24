package com.revolut.core.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revolut.dao.model.Currency;
import com.revolut.dao.model.TransactionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionDto {

    private String id;
    private TransactionType type;
    private String description;
    private BigDecimal amount;
    private Currency currency;
    @Setter
    private LocalDateTime createdAt;

}

package com.revolut.dao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class Transaction extends DomainEntity {

    @Setter
    private String uuid;
    private Long ownerAccountId;
    private Long otherAccountId;
    private TransactionType type;
    private String description;
    private BigDecimal amount;
    private Currency currency;
    @Setter
    private LocalDateTime createdAt;

}

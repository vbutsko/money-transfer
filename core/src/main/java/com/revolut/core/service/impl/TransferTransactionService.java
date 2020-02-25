package com.revolut.core.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.revolut.core.converter.TransactionEntityToDtoConverter;
import com.revolut.core.dto.TransactionDto;
import com.revolut.core.dto.TransferTransactionCreateDto;
import com.revolut.core.exception.NotFoundException;
import com.revolut.core.exception.TransactionFailedException;
import com.revolut.core.exception.ValidationException;
import com.revolut.core.service.TransactionService;
import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dao.exception.DaoValidationException;
import com.revolut.dao.model.Account;
import com.revolut.dao.model.Transaction;
import com.revolut.dao.transation.InMemoryTransactionDao;
import com.revolut.dao.transation.TransactionDao;

import static com.revolut.dao.model.TransactionType.TRANSFER_BETWEEN_ACCOUNTS;

public class TransferTransactionService implements TransactionService<TransferTransactionCreateDto, TransactionDto> {

    private static TransactionService instance;

    private AccountDao accountDao;

    private TransactionDao transactionDao;

    private TransactionEntityToDtoConverter converter;

    private TransferTransactionService() {
        transactionDao = InMemoryTransactionDao.getInstance();
        accountDao = InMemoryAccountDao.getInstance();
        converter = new TransactionEntityToDtoConverter();
    }

    public synchronized static TransactionService getInstance() {
        if (instance == null) {
            instance = new TransferTransactionService();
        }
        return instance;
    }

    @Override
    public TransactionDto createTransaction(TransferTransactionCreateDto createDto) {
        validateTransactionCreateDto(createDto);
        Account from = getAccount(createDto.getAccountId());
        Account to = getAccount(createDto.getDestinationAccountId());
        verifyTransaction(from, to, createDto);
        Transaction toTransaction = Transaction.builder()
                .ownerAccountId(from.getId())
                .otherAccountId(to.getId())
                .amount(createDto.getAmount())
                .type(TRANSFER_BETWEEN_ACCOUNTS)
                .currency(from.getCurrency())
                .build();
        Transaction fromTransaction = Transaction.builder()
                .ownerAccountId(to.getId())
                .otherAccountId(from.getId())
                .amount(createDto.getAmount().multiply(BigDecimal.valueOf(-1)))
                .type(TRANSFER_BETWEEN_ACCOUNTS)
                .currency(from.getCurrency())
                .build();
        from.setTotal(from.getTotal().add(fromTransaction.getAmount()));
        to.setTotal(to.getTotal().add(toTransaction.getAmount()));
        try {
            toTransaction = transactionDao.save(toTransaction);
            fromTransaction = transactionDao.save(fromTransaction);
        } catch (DaoValidationException ex) {
            rollBackTransactions(Arrays.asList(toTransaction, fromTransaction));
        }
        if (!accountDao.updateAccounts(Arrays.asList(from, to))) {
            // TODO transaction should not be removed but reverted by creating new one
            rollBackTransactions(Arrays.asList(toTransaction, fromTransaction));

        }
        return converter.convert(fromTransaction);
    }

    private Account getAccount(String accountId) {
        return accountDao.getEntity(accountId)
                .orElseThrow(() -> new NotFoundException(String.format("Account with id %s not found", accountId)));
    }

    private void validateTransactionCreateDto(TransferTransactionCreateDto createDto) {
        if (createDto.getDestinationAccountId() == null || createDto.getAmount() == null) {
            throw new ValidationException("Transaction should contain not null and valid destination account id and amount");
        } else if (createDto.getDestinationAccountId().equals(createDto.getAccountId())) {
            throw new ValidationException("Operation with same account id is not supported");
        } else if (createDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transaction amount should have positive value");
        }
    }

    private void verifyTransaction(Account from, Account to, TransferTransactionCreateDto createDto) {
        if (!from.getCurrency().equals(to.getCurrency())) {
            throw new TransactionFailedException("Transaction failed cause accounts have different currencies");
        } else if (from.getTotal().compareTo(createDto.getAmount()) < 0) {
            throw new TransactionFailedException(String.format("Transaction failed cause not enough money on account %s", from.getUuid()));
        }

    }

    private void rollBackTransactions(List<Transaction> transactions) {
        transactions.stream().map(Transaction::getUuid).forEach(transactionDao::delete);
        throw new TransactionFailedException("Transaction failed and rolled back");
    }

}

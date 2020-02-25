package com.revolut.core.service.impl;

import java.util.List;
import java.util.UUID;

import com.revolut.core.converter.AccountEntityToDtoConverter;
import com.revolut.core.dto.AccountCreateDto;
import com.revolut.core.dto.AccountDto;
import com.revolut.core.exception.NotFoundException;
import com.revolut.core.exception.ValidationException;
import com.revolut.core.service.AccountService;
import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dao.exception.DaoValidationException;
import com.revolut.dao.model.Account;

import static java.util.Collections.emptyList;

public class DefaultAccountService implements AccountService {

    private static volatile AccountService instance;

    private AccountDao accountDao;

    private AccountEntityToDtoConverter accountEntityToDtoConverter;

    public static synchronized AccountService getInstance() {
        if (instance == null) {
            instance = new DefaultAccountService();
        }
        return instance;
    }

    private DefaultAccountService() {
        accountDao = InMemoryAccountDao.getInstance();
        accountEntityToDtoConverter = new AccountEntityToDtoConverter();
    }

    @Override
    public List<AccountDto> getAll() {
        return accountEntityToDtoConverter.convertCollection(accountDao.getAll());
    }

    @Override
    public AccountDto getById(String id) {
        return accountDao.getEntity(id).map(accountEntityToDtoConverter::convert)
                .orElseThrow(() -> new NotFoundException(String.format("Account with id %s not found", id)));
    }

    @Override
    public AccountDto createAccount(AccountCreateDto createDto) {
        validateAccountDetails(createDto);
        Account account = Account.builder()
                .currency(createDto.getCurrency())
                .total(createDto.getTotal())
                .transactionHistory(emptyList())
                .build();
        try {
            account = accountDao.save(account);
        } catch (DaoValidationException ex) {
            throw new ValidationException("To create account you should set not null and valid currency and total");
        }
        return accountEntityToDtoConverter.convert(account);
    }

    private void validateAccountDetails(AccountCreateDto createDto) {
        if (createDto.getCurrency() == null || createDto.getTotal() == null) {
            throw new ValidationException("To create account you should set not null and valid currency and total");
        }
    }

}

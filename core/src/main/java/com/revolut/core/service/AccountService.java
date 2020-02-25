package com.revolut.core.service;

import java.util.List;
import java.util.UUID;

import com.revolut.core.converter.AccountEntityToDtoConverter;
import com.revolut.core.dto.AccountCreateDto;
import com.revolut.core.dto.AccountDto;
import com.revolut.core.exception.NotFoundException;
import com.revolut.core.exception.ValidationException;
import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dao.model.Account;

import static java.util.Collections.emptyList;

public class AccountService {

    private static volatile AccountService instance;

    private AccountDao accountDao;

    private AccountEntityToDtoConverter accountEntityToDtoConverter;

    public static synchronized AccountService getInstance() {
        if (instance == null) {
            instance = new AccountService();
        }
        return instance;
    }

    private AccountService() {
        accountDao = InMemoryAccountDao.getInstance();
        accountEntityToDtoConverter = new AccountEntityToDtoConverter();
    }

    public List<AccountDto> getAll() {
        return accountEntityToDtoConverter.convertCollection(accountDao.getAll());
    }

    public AccountDto getById(String id) {
        return accountDao.getEntity(id).map(accountEntityToDtoConverter::convert)
                .orElseThrow(() -> new NotFoundException(String.format("Account with id %s not found", id)));
    }

    public AccountDto createAccount(AccountCreateDto createDto) {
        validateAccountDetails(createDto);
        Account account = Account.builder()
                .uuid(UUID.randomUUID().toString())
                .currency(createDto.getCurrency())
                .total(createDto.getTotal())
                .transactionHistory(emptyList())
                .build();
        return accountEntityToDtoConverter.convert(accountDao.save(account));
    }

    private void validateAccountDetails(AccountCreateDto createDto) {
        if (createDto.getCurrency() == null || createDto.getTotal() == null) {
            throw new ValidationException("To create account you should set not null and valid currency and total");
        }
    }

}

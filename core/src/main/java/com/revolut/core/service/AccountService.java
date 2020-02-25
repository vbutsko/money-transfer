package com.revolut.core.service;

import java.util.List;

import com.revolut.core.converter.AccountEntityToDtoConverter;
import com.revolut.core.exception.NotFoundException;
import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.core.dto.AccountDto;

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

}

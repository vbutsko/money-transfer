package com.revolut.core.service;

import java.util.List;

import com.revolut.core.converter.AccountEntityToDtoConverter;
import com.revolut.core.dto.AccountDto;
import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;

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

}

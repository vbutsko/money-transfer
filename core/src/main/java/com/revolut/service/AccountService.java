package com.revolut.service;

import java.util.Optional;

import com.revolut.converter.AccountEntityToDtoConverter;
import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dto.AccountDto;

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

    public Optional<AccountDto> getById(String id) {
        return accountDao.getEntity(id).map(accountEntityToDtoConverter::convert);
    }

}

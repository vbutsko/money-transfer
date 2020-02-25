package com.revolut.core.service;

import java.util.List;

import com.revolut.core.dto.AccountCreateDto;
import com.revolut.core.dto.AccountDto;

public interface AccountService {

    List<AccountDto> getAll();

    AccountDto getById(String id);

    AccountDto createAccount(AccountCreateDto createDto);

}

package com.revolut.dao.account;

import java.util.List;

import com.revolut.dao.Dao;
import com.revolut.dao.model.Account;

public interface AccountDao extends Dao<Account> {

    List<Account> getAll();

    boolean updateAccounts(List<Account> accounts);

}

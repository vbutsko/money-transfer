package com.revolut.dao.transation;

import java.util.List;

import com.revolut.dao.Dao;
import com.revolut.model.Transaction;

public interface TransactionDao extends Dao<Transaction> {

    List<Transaction> getEntities(Long accountId);

}

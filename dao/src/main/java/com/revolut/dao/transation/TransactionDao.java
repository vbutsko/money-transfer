package com.revolut.dao.transation;

import java.util.List;

import com.revolut.dao.model.Transaction;
import com.revolut.dao.Dao;

public interface TransactionDao extends Dao<Transaction> {

    List<Transaction> getEntities(Long accountId);

}

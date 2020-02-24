package com.revolut.dao.account;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


import com.revolut.dao.InMemoryDao;
import com.revolut.dao.exception.ValidationException;
import com.revolut.dao.transation.InMemoryTransactionDao;
import com.revolut.dao.transation.TransactionDao;
import com.revolut.dao.model.Account;

//TODO: delete operation should save history in some way
public class InMemoryAccountDao extends InMemoryDao<Account> implements AccountDao {

    private static volatile AtomicLong dbIdGenerator = new AtomicLong(1);

    private static volatile InMemoryAccountDao instance;

    private TransactionDao transactionDao;

    private InMemoryAccountDao() {
        transactionDao = InMemoryTransactionDao.getInstance();
    }

    public synchronized static InMemoryAccountDao getInstance() {
        if (instance == null) {
            instance = new InMemoryAccountDao();
        }
        return instance;
    }

    @Override
    public Optional<Account> getEntity(String uuid) {
        return entities.stream()
                .filter(account -> Objects.equals(account.getUuid(), uuid))
                .findFirst()
                .map(this::copy)
                .map(this::setTransactionHistory);
    }

    @Override
    public Account save(Account account) {
        // validate account
        validate(account);
        Account copy = copy(account);
        int accountIndex = entities.size();
        synchronized (entities) {
            for (int i = 0; i < entities.size(); i++) {
                if (Objects.equals(entities.get(i).getUuid(), copy.getUuid())) {
                    accountIndex = i;
                    break;
                }
            }
            if (accountIndex < entities.size()) {
                copy.setId(entities.get(accountIndex).getId());
                entities.set(accountIndex, copy);
            } else {
                copy.setId(dbIdGenerator.getAndIncrement());
                entities.add(copy);
            }
        }
        copy = copy(entities.get(accountIndex));
        return setTransactionHistory(copy);
    }

    @Override
    public Optional<Account> delete(String uuid) {
        Optional<Account> removed = Optional.empty();
        synchronized (entities) {
            Iterator<Account> accountIterator = entities.iterator();
            while (accountIterator.hasNext()) {
                Account account = accountIterator.next();
                if (Objects.equals(account.getUuid(), uuid)) {
                    accountIterator.remove();
                    removed = Optional.of(account);
                    break;
                }
            }
        }
        return removed;
    }

    @Override
    public List<Account> deleteAll() {
        List<Account> removed;
        synchronized (entities) {
            removed = new ArrayList<>(entities);
            entities.clear();
        }
        return removed;
    }

    @Override
    public List<Account> getAll() {
        synchronized (entities) {
            return entities.stream()
                    .map(this::copy)
                    .map(this::setTransactionHistory)
                    .collect(Collectors.toList());
        }
    }

    private Account copy(Account account) {
        Account copy = Account.builder()
                .uuid(account.getUuid())
                .currency(account.getCurrency())
                .total(account.getTotal())
                .build();
        copy.setId(account.getId());
        return copy;
    }

    private Account setTransactionHistory(Account account) {
        account.setTransactionHistory(transactionDao.getEntities(account.getId()));
        return account;
    }

    private void validate(Account account) {
        if (account.getUuid() == null || account.getCurrency() == null) {
            throw new ValidationException("Account uuid and currency can't be null");
        }
    }

}

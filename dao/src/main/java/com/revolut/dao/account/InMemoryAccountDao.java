package com.revolut.dao.account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.revolut.dao.InMemoryDao;
import com.revolut.dao.exception.ValidationException;
import com.revolut.model.Account;
import com.revolut.model.Transaction;

public class InMemoryAccountDao extends InMemoryDao<Account> implements AccountDao {

    private static volatile InMemoryAccountDao instance;

    private InMemoryAccountDao() {

    }

    public static InMemoryAccountDao getInstance() {
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
                .map(this::copyAccount)
                .map(account -> {
                    account.setTransactionHistory(getTransactionHistory(account.getUuid()));
                    return account;
                });
    }

    @Override
    public Account save(Account account) {
        // validate account
        validate(account);
        Account copy = copyAccount(account);
        int accountIndex = entities.size();
        synchronized (entities) {
            for (int i = 0; i < entities.size(); i++) {
                if (Objects.equals(entities.get(i).getUuid(), copy.getUuid())) {
                    accountIndex = i;
                    break;
                }
            }
            if (accountIndex < entities.size()) {
                entities.set(accountIndex, copy);
            } else {
                entities.add(copy);
            }
        }
        return entities.get(accountIndex);
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

    private Account copyAccount(Account account) {
        return Account.builder()
                .uuid(account.getUuid())
                .currency(account.getCurrency())
                .total(account.getTotal())
                .build();

    }

    private List<Transaction> getTransactionHistory(String uuid) {
        return Collections.emptyList();
    }

    private void validate(Account account) {
        if (account.getUuid() == null || account.getCurrency() == null) {
            throw new ValidationException("Account uuid and currency can't be null");
        }
    }

}

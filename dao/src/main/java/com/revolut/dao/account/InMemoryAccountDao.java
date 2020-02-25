package com.revolut.dao.account;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.revolut.dao.InMemoryDao;
import com.revolut.dao.exception.DaoValidationException;
import com.revolut.dao.model.Account;
import com.revolut.dao.transation.InMemoryTransactionDao;
import com.revolut.dao.transation.TransactionDao;

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
    public Account save(Account account) throws DaoValidationException {
        validate(account);
        Account copy = copy(account);
        if (copy.getUuid() == null) {
            copy.setUuid(UUID.randomUUID().toString());
        }
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

    @Override
    public boolean updateAccounts(List<Account> accounts) {
        synchronized (entities) {
            List<Account> validForUpdate = accounts.stream()
                    .map(account -> getEntity(account.getUuid())
                            .filter(savedAccount -> savedAccount.getTransactionHistory().size() - 1 == account.getTransactionHistory()
                                    .size()
                                    && savedAccount.getTransactionHistory().containsAll(account.getTransactionHistory())))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            if (validForUpdate.size() == accounts.size()) {
                try {
                    for (Account account : accounts) {
                        save(account);
                    }
                    return true;
                } catch (DaoValidationException e) {
                    rollBack(validForUpdate);
                }
            }
            return false;
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

    private void validate(Account account) throws DaoValidationException {
        if (account.getUuid() == null || account.getCurrency() == null) {
            throw new DaoValidationException("Account uuid and currency can't be null");
        }
    }

    private void rollBack(List<Account> accounts) {
        // should be logic to revert to previous db state as part of transaction
        try {
            for (Account account : accounts) {
                save(account);
            }
        } catch (DaoValidationException ex) {
            throw new Error("Application failed");
        }
    }

}

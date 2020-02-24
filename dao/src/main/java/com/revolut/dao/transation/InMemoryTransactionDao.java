package com.revolut.dao.transation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.revolut.dao.InMemoryDao;
import com.revolut.dao.model.Transaction;
import com.revolut.dao.exception.ValidationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryTransactionDao extends InMemoryDao<Transaction> implements TransactionDao {

    private static volatile AtomicLong dbIdGenerator = new AtomicLong(1);

    private static volatile InMemoryTransactionDao instance;

    public synchronized static InMemoryTransactionDao getInstance() {
        if (instance == null) {
            instance = new InMemoryTransactionDao();
        }
        return instance;
    }

    @Override
    public List<Transaction> getEntities(Long accountId) {
        synchronized (entities) {
            return entities.stream()
                    .filter(transaction -> Objects.equals(transaction.getAccountId(), accountId))
                    .collect(toList());
        }
    }

    @Override
    public Optional<Transaction> getEntity(String uuid) {
        synchronized (entities) {
            return entities.stream()
                    .filter(transaction -> Objects.equals(transaction.getUuid(), uuid))
                    .findFirst()
                    .map(this::copy);
        }
    }

    @Override
    public Transaction save(Transaction transaction) {
        validate(transaction);
        Transaction copy = copy(transaction);
        int newIndex;
        synchronized (entities) {
            copy.setId(dbIdGenerator.getAndIncrement());
            copy.setCreatedAt(LocalDateTime.now());
            entities.add(copy);
            newIndex = entities.size() - 1;
        }
        return copy(entities.get(newIndex));
    }

    @Override
    public Optional<Transaction> delete(String uuid) {
        throw new UnsupportedOperationException("Transaction can't be removed");
    }

    @Override
    // left for tests
    public List<Transaction> deleteAll() {
        synchronized (entities) {
            List<Transaction> removed = new ArrayList<>(entities);
            entities.clear();
            return removed;
        }
    }

    private void validate(Transaction transaction) {
        List<String> validationErrors = new ArrayList<>();
        if (transaction.getId() != null) {
            validationErrors.add("id is not null");
        }
        if (transaction.getCurrency() == null) {
            validationErrors.add("currency is null");
        }
        if (transaction.getAccountId() == null) {
            validationErrors.add("accountId is null");
        }
        if (transaction.getAmount() == null) {
            validationErrors.add("amount is null");
        }
        if (transaction.getType() == null) {
            validationErrors.add("transaction type is null");
        }
        if (!validationErrors.isEmpty()) {
            String message = String.format("Validation for transaction is failed cause: %s.",
                    String.join("; ", validationErrors));
            throw new ValidationException(message);
        }
    }

    private Transaction copy(Transaction transaction) {
        Transaction copy = Transaction.builder()
                .uuid(transaction.getUuid())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
        copy.setId(transaction.getId());
        return copy;
    }

}

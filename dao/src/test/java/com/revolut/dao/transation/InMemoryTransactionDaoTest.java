package com.revolut.dao.transation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.revolut.dao.model.Currency;
import com.revolut.dao.model.Transaction;
import com.revolut.dao.model.TransactionType;
import com.revolut.dao.exception.DaoValidationException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryTransactionDaoTest {

    private static final String UUID_1 = "transaction-1";
    private static final String UUID_2 = "transaction-2";
    private static final String UUID_3 = "transaction-3";
    private static final Long ACCOUNT_ID_1 = 1L;
    private static final Long ACCOUNT_ID_2 = 2L;
    private static final String DESCRIPTION_1 = "desc-1";
    private static final String DESCRIPTION_2 = "desc-2";
    private static final String DESCRIPTION_3 = "desc-3";
    private static final BigDecimal AMOUNT_1 = BigDecimal.valueOf(100);
    private static final BigDecimal AMOUNT_2 = BigDecimal.valueOf(200);
    private static final BigDecimal AMOUNT_3 = BigDecimal.valueOf(-100);
    private static final Currency CURRENCY_1 = Currency.USD;
    private static final Currency CURRENCY_2 = Currency.EUR;
    private static final Currency CURRENCY_3 = Currency.USD;
    private static final TransactionType TYPE_1 = TransactionType.TRANSFER_BETWEEN_ACCOUNTS;
    private static final TransactionType TYPE_2 = TransactionType.CONVERSATION;
    private static final TransactionType TYPE_3 = TransactionType.TRANSFER_BETWEEN_ACCOUNTS;

    private InMemoryTransactionDao transactionDao;

    private Transaction transaction1;
    private Transaction transaction2;

    @Before
    public void setUp() {
        transactionDao = InMemoryTransactionDao.getInstance();
        transactionDao.deleteAll();
        transaction1 = transactionDao.save(Transaction.builder()
                .uuid(UUID_1)
                .accountId(ACCOUNT_ID_1)
                .amount(AMOUNT_1)
                .description(DESCRIPTION_1)
                .currency(CURRENCY_1)
                .type(TYPE_1)
                .build());
        transaction2 = transactionDao.save(Transaction.builder()
                .uuid(UUID_2)
                .accountId(ACCOUNT_ID_2)
                .amount(AMOUNT_2)
                .description(DESCRIPTION_2)
                .currency(CURRENCY_2)
                .type(TYPE_2)
                .build());
    }

    @Test
    public void shouldAddNewTransaction() {
        // given
        Transaction newTransaction = Transaction.builder()
                .uuid(UUID_3)
                .accountId(ACCOUNT_ID_1)
                .amount(BigDecimal.ONE)
                .description(DESCRIPTION_3)
                .currency(CURRENCY_3)
                .type(TYPE_3)
                .build();

        // when
        Transaction result = transactionDao.save(newTransaction);

        // then
        assertThat(newTransaction).isEqualToIgnoringGivenFields(result, "id", "createdAt");
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test(expected = DaoValidationException.class)
    public void shouldThrowValidationExceptionCauseIdIsNotNull() {
        // when
        transactionDao.save(transaction1);
    }

    @Test(expected = DaoValidationException.class)
    public void shouldThrowValidationExceptionCauseCurrencyMissed() {
        // when
        transactionDao.save(Transaction.builder()
                .uuid(UUID_3)
                .accountId(ACCOUNT_ID_1)
                .amount(AMOUNT_3)
                .description(DESCRIPTION_3)
                .type(TYPE_3)
                .build());
    }

    @Test(expected = DaoValidationException.class)
    public void shouldThrowValidationExceptionCauseAccountIdMissed() {
        // when
        transactionDao.save(Transaction.builder()
                .uuid(UUID_3)
                .amount(AMOUNT_3)
                .description(DESCRIPTION_3)
                .currency(CURRENCY_3)
                .type(TYPE_3)
                .build());
    }

    @Test(expected = DaoValidationException.class)
    public void shouldThrowValidationExceptionCauseAmountMissed() {
        // when
        transactionDao.save(Transaction.builder()
                .uuid(UUID_3)
                .accountId(ACCOUNT_ID_1)
                .description(DESCRIPTION_3)
                .currency(CURRENCY_3)
                .type(TYPE_3)
                .build());
    }

    @Test(expected = DaoValidationException.class)
    public void shouldThrowValidationExceptionCauseTypeMissed() {
        // when
        transactionDao.save(Transaction.builder()
                .uuid(UUID_3)
                .accountId(ACCOUNT_ID_1)
                .amount(AMOUNT_3)
                .description(DESCRIPTION_3)
                .currency(CURRENCY_3)
                .build());
    }

    @Test
    public void shouldFindTransactionsByAccountId() {
        // given
        Transaction transaction = transactionDao.save(Transaction.builder()
                .uuid(UUID_3)
                .accountId(ACCOUNT_ID_1)
                .amount(AMOUNT_3)
                .description(DESCRIPTION_3)
                .currency(CURRENCY_3)
                .type(TYPE_3)
                .build());


        // when
        List<Transaction> resultList = transactionDao.getEntities(ACCOUNT_ID_1);

        // then
        assertThat(resultList).containsExactlyInAnyOrder(transaction, transaction1);
    }

    @Test
    public void shouldNotFindTransactionsByAccountId() {
        // when
        List<Transaction> resultList = transactionDao.getEntities(12L);

        // then
        assertThat(resultList).isEmpty();
    }

    @Test
    public void shouldFindTransactionByUuid() {
        // when
        Optional<Transaction> result = transactionDao.getEntity(UUID_1);

        // then
        assertThat(result).isPresent().get().isEqualTo(transaction1);
    }

    @Test
    public void shouldNotFindTransactionByUuid() {
        // when
        Optional<Transaction> result = transactionDao.getEntity(UUID_3);

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    public void getInstanceReturnSameObject() {
        // when
        InMemoryTransactionDao instance = InMemoryTransactionDao.getInstance();

        // then
        assertThat(instance).isEqualTo(transactionDao);
    }

}
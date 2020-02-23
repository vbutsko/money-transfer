package com.revolut.dao.account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.revolut.model.Account;
import com.revolut.model.Currency;
import com.revolut.model.Transaction;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryAccountDaoTest {

    private static final String UUID_1 = "account-1";
    private static final String UUID_2 = "account-2";
    private static final BigDecimal TOTAL_1 = BigDecimal.valueOf(100);
    private static final BigDecimal TOTAL_2 = BigDecimal.valueOf(200);
    private static final Currency CURRENCY_1 = Currency.USD;
    private static final Currency CURRENCY_2 = Currency.EUR;
    private static final List<Transaction> TRANSACTIONS_1 = emptyList();
    private static final List<Transaction> TRANSACTIONS_2 = singletonList(
            Transaction.builder()
                    .build()
    );
    private Account account1;
    private Account account2;

    private InMemoryAccountDao accountDao;

    @Before
    public void setUp() {
        accountDao = InMemoryAccountDao.getInstance();
        accountDao.deleteAll();
        account1 = Account.builder()
                .uuid(UUID_1)
                .total(TOTAL_1)
                .currency(CURRENCY_1)
                .build();
        account2 = Account.builder()
                .uuid(UUID_2)
                .total(TOTAL_2)
                .currency(CURRENCY_2)
                .build();
        accountDao.save(account1);
        accountDao.save(account2);
    }

    @Test
    public void shouldAddNewAccount() {
        // given
        Account newAccount = Account.builder()
                .uuid("account-3")
                .currency(Currency.USD)
                .total(BigDecimal.ONE)
                .build();

        // when
        Account result = accountDao.save(newAccount);

        // then
        assertThat(newAccount).isEqualTo(result);
    }

    @Test
    public void shouldReplaceAccount() {
        // given
        Account updatedAccount = Account.builder()
                .uuid(UUID_2)
                .total(BigDecimal.ONE)
                .currency(Currency.USD)
                .transactionHistory(emptyList())
                .build();

        // when
        Account result = accountDao.save(updatedAccount);
        Optional<Account> optionalAccount = accountDao.getEntity(UUID_2);

        // then
        assertThat(updatedAccount).isEqualTo(result);
        assertThat(optionalAccount).isPresent().get().isEqualTo(updatedAccount);
    }

    @Test
    public void shouldNotUpdateTransactionHistory() {
        // given
        Account updatedAccount = Account.builder()
                .uuid(UUID_1)
                .total(BigDecimal.ONE)
                .currency(Currency.USD)
                .transactionHistory(TRANSACTIONS_2)
                .build();

        // when
        Account result = accountDao.save(updatedAccount);
        Optional<Account> optionalAccount = accountDao.getEntity(UUID_1);

        // then
        assertThat(result.getTransactionHistory()).isEqualTo(TRANSACTIONS_1);
        assertThat(optionalAccount.map(Account::getTransactionHistory)).isPresent().get().isEqualTo(TRANSACTIONS_1);
    }

    @Test
    public void shouldGetAccount() {
        // when
        Optional<Account> result = accountDao.getEntity(UUID_1);

        // then
        assertThat(result).isPresent().get().isEqualTo(account1);
    }

    @Test
    public void shouldNotGetAccount() {
        // when
        Optional<Account> result = accountDao.getEntity("not-existed-account");

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    public void shouldDeleteAccount() {
        // when
        Optional<Account> result = accountDao.delete(UUID_1);
        Optional<Account> optionalAccount1 = accountDao.getEntity(UUID_1);

        // then
        assertThat(result).isPresent().get().isEqualTo(account1);
        assertThat(optionalAccount1).isNotPresent();
    }

    @Test
    public void shouldReturnEmptyOptionalWhenNoAccount() {
        // when
        Optional<Account> result = accountDao.delete("deleted-account");

        // then
        assertThat(result).isNotPresent();
    }

    @Test
    public void shouldDeleteAll() {
        // when
        List<Account> resultList = accountDao.deleteAll();
        Optional<Account> optionalAccount1 = accountDao.getEntity(UUID_1);
        Optional<Account> optionalAccount2 = accountDao.getEntity(UUID_2);

        // then
        assertThat(resultList).containsExactlyInAnyOrder(account1, account2);
        assertThat(optionalAccount1).isNotPresent();
        assertThat(optionalAccount2).isNotPresent();
    }

    @Test
    public void shouldReturnEmptyCollectionIfNothingToDelete() {
        // given
        accountDao.deleteAll();

        // when
        List<Account> resultList = accountDao.deleteAll();

        // then
        assertThat(resultList).isEmpty();

    }

    @Test
    public void getInstanceReturnSameObject() {
        // when
        InMemoryAccountDao instance = InMemoryAccountDao.getInstance();

        // then
        assertThat(instance).isEqualTo(accountDao);
    }

}
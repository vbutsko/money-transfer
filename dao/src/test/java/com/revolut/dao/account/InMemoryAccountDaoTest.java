package com.revolut.dao.account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.revolut.dao.model.Currency;
import com.revolut.dao.model.Transaction;
import com.revolut.dao.exception.DaoValidationException;
import com.revolut.dao.transation.InMemoryTransactionDao;
import com.revolut.dao.model.Account;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InMemoryTransactionDao.class})
public class InMemoryAccountDaoTest {

    private static final String UUID_1 = "account-1";
    private static final String UUID_2 = "account-2";
    private static final BigDecimal TOTAL_1 = BigDecimal.valueOf(100);
    private static final BigDecimal TOTAL_2 = BigDecimal.valueOf(200);
    private static final Currency CURRENCY_1 = Currency.USD;
    private static final Currency CURRENCY_2 = Currency.EUR;
    private static final List<Transaction> TRANSACTIONS_2 = singletonList(
            Transaction.builder()
                    .build()
    );
    private Account account1;
    private Account account2;

    private static InMemoryTransactionDao transactionDao;

    private InMemoryAccountDao accountDao;

    @BeforeClass
    public static void globalSetUp() throws Exception {
        transactionDao = mock(InMemoryTransactionDao.class);
        PowerMockito.whenNew(InMemoryTransactionDao.class).withAnyArguments().thenReturn(transactionDao);
    }

    @Before
    public void setUp() throws DaoValidationException {
        accountDao = InMemoryAccountDao.getInstance();
        accountDao.deleteAll();
        account1 = accountDao.save(Account.builder()
                .uuid(UUID_1)
                .total(TOTAL_1)
                .currency(CURRENCY_1)
                .build());
        account2 = accountDao.save(Account.builder()
                .uuid(UUID_2)
                .total(TOTAL_2)
                .currency(CURRENCY_2)
                .build());
    }

    @Test
    public void shouldAddNewAccount() throws DaoValidationException {
        // given
        Account newAccount = Account.builder()
                .uuid("account-3")
                .currency(Currency.USD)
                .total(BigDecimal.ONE)
                .build();

        // when
        Account result = accountDao.save(newAccount);

        // then
        assertThat(newAccount).isEqualToIgnoringGivenFields(result, "id");
    }

    @Test
    public void shouldReplaceAccount() throws DaoValidationException {
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
        assertThat(updatedAccount).isEqualToIgnoringGivenFields(result, "id");
        assertThat(optionalAccount).isPresent().get().isEqualToIgnoringGivenFields(updatedAccount, "id");
    }

    @Test(expected = DaoValidationException.class)
    public void shouldThrowValidationException() throws DaoValidationException {
        // when
        accountDao.save(Account.builder().build());
    }

    @Test
    public void shouldNotUpdateTransactionHistory() throws DaoValidationException {
        // given
        Account updatedAccount = Account.builder()
                .uuid(UUID_2)
                .total(BigDecimal.ONE)
                .currency(Currency.USD)
                .transactionHistory(singletonList(Transaction.builder().build()))
                .build();
        when(transactionDao.getEntities(account2.getId())).thenReturn(TRANSACTIONS_2);

        // when
        Account result = accountDao.save(updatedAccount);
        Optional<Account> optionalAccount = accountDao.getEntity(UUID_2);

        // then
        assertThat(result.getTransactionHistory()).isEqualTo(TRANSACTIONS_2);
        assertThat(optionalAccount.map(Account::getTransactionHistory)).isPresent().get().isEqualTo(TRANSACTIONS_2);
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

    @Test
    public void shouldGetAll() {
        // when
        List<Account> result = accountDao.getAll();

        // then
        assertThat(result).containsExactlyInAnyOrder(account1, account2);
    }

    @Test
    public void shouldReturnEmptyCollectionIfNoAccounts() {
        // given
        accountDao.deleteAll();

        // when
        List<Account> result = accountDao.getAll();

        // then
        assertThat(result).isEmpty();
    }

}
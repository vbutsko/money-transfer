package com.revolut.core;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.revolut.core.dto.AccountCreateDto;
import com.revolut.core.dto.AccountDto;
import com.revolut.core.dto.TransactionDto;
import com.revolut.core.exception.NotFoundException;
import com.revolut.core.exception.ValidationException;
import com.revolut.core.service.AccountService;
import com.revolut.core.service.impl.DefaultAccountService;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dao.exception.DaoValidationException;
import com.revolut.dao.model.Account;
import com.revolut.dao.model.Currency;
import com.revolut.dao.model.Transaction;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InMemoryAccountDao.class})
public class DefaultAccountServiceTest {

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

    private AccountService defaultAccountService = DefaultAccountService.getInstance();

    private static InMemoryAccountDao accountDao;

    private Account account1;
    private Account account2;

    @BeforeClass
    public static void globalSetUp() throws Exception {
        accountDao = mock(InMemoryAccountDao.class);
        PowerMockito.whenNew(InMemoryAccountDao.class).withAnyArguments().thenReturn(accountDao);
    }

    @Before
    public void setUp() {
        account1 = Account.builder()
                .uuid(UUID_1)
                .total(TOTAL_1)
                .currency(CURRENCY_1)
                .build();
        account2 = Account.builder()
                .uuid(UUID_2)
                .total(TOTAL_2)
                .currency(CURRENCY_2)
                .transactionHistory(TRANSACTIONS_2)
                .build();
    }

    @Test
    public void shouldReturnAll() {
        // given
        when(accountDao.getAll()).thenReturn(Arrays.asList(account1, account2));

        // when
        List<AccountDto> result = defaultAccountService.getAll();

        // then
        List<AccountDto> expected = Arrays.asList(
                AccountDto.builder()
                        .id(UUID_1)
                        .currency(CURRENCY_1)
                        .total(TOTAL_1)
                        .transactionHistory(emptyList())
                        .build(),
                AccountDto.builder()
                        .id(UUID_2)
                        .currency(CURRENCY_2)
                        .total(TOTAL_2)
                        .transactionHistory(singletonList(TransactionDto.builder().build()))
                        .build()
        );
        assertThat(result).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrderElementsOf(expected);

    }

    @Test
    public void shouldReturnEmptyCollectionIfNoAccounts() {
        // given
        when(accountDao.getAll()).thenReturn(emptyList());

        // when
        List<AccountDto> result = defaultAccountService.getAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfAcocuntNotFound() {
        // when
        defaultAccountService.getById(UUID_1);
    }

    @Test
    public void shouldGetAccount() {
        // given
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.of(account1));

        // when
        AccountDto result = defaultAccountService.getById(UUID_1);

        //then
        AccountDto expected = AccountDto.builder()
                .id(UUID_1)
                .currency(CURRENCY_1)
                .total(TOTAL_1)
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void shouldCreateAccount() throws DaoValidationException {
        // given
        AccountCreateDto createDto = AccountCreateDto.builder()
                .currency(CURRENCY_1)
                .total(TOTAL_1)
                .build();
        when(accountDao.save(Account.builder()
                .total(TOTAL_1)
                .currency(CURRENCY_1)
                .build())
        ).thenReturn(account1);

        // when
        AccountDto result = defaultAccountService.createAccount(createDto);

        // then
        AccountDto expected = AccountDto.builder()
                .id(UUID_1)
                .currency(CURRENCY_1)
                .total(TOTAL_1)
                .transactionHistory(emptyList())
                .build();
        assertThat(result).isEqualTo(expected);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowValidationExceptionIfCatchDaoException() throws DaoValidationException {
        // given
        when(accountDao.save(any(Account.class))).thenThrow(DaoValidationException.class);

        // when
        defaultAccountService.createAccount(AccountCreateDto.builder().build());
    }

}
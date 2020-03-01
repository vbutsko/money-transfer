package com.revolut.core.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import com.revolut.core.dto.TransactionDto;
import com.revolut.core.dto.TransferTransactionCreateDto;
import com.revolut.core.exception.NotFoundException;
import com.revolut.core.exception.TransactionFailedException;
import com.revolut.core.exception.ValidationException;
import com.revolut.core.service.TransactionService;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dao.exception.DaoValidationException;
import com.revolut.dao.model.Account;
import com.revolut.dao.model.Currency;
import com.revolut.dao.model.Transaction;
import com.revolut.dao.transation.InMemoryTransactionDao;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.revolut.dao.model.TransactionType.TRANSFER_BETWEEN_ACCOUNTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({InMemoryAccountDao.class, InMemoryTransactionDao.class})
public class TransferTransactionServiceTest {

    private static final String UUID_1 = "account-1";
    private static final String UUID_2 = "account-2";
    private static final BigDecimal TOTAL_1 = BigDecimal.valueOf(100);
    private static final BigDecimal TOTAL_2 = BigDecimal.valueOf(200);
    private static final Currency CURRENCY_1 = Currency.USD;

    private static InMemoryAccountDao accountDao;
    private static InMemoryTransactionDao transactionDao;
    private static Account account1;
    private static Account account2;

    private TransactionService transferTransactionService = TransferTransactionService.getInstance();

    @BeforeClass
    public static void globalSetUp() throws Exception {
        accountDao = mock(InMemoryAccountDao.class);
        PowerMockito.whenNew(InMemoryAccountDao.class).withAnyArguments().thenReturn(accountDao);
        transactionDao = mock(InMemoryTransactionDao.class);
        PowerMockito.whenNew(InMemoryTransactionDao.class).withAnyArguments().thenReturn(transactionDao);
        account1 = Account.builder()
                .uuid(UUID_1)
                .total(TOTAL_1)
                .currency(CURRENCY_1)
                .build();
        account2 = Account.builder()
                .uuid(UUID_2)
                .total(TOTAL_2)
                .currency(CURRENCY_1)
                .build();
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionIfDestinationIdIsNull() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_2);

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionIfAccountIdIsNull() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionIfAccountIdsAreEquals() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_1);

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionIfAmountIsNull() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAccountId(UUID_2);

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = ValidationException.class)
    public void shouldThrowExceptionIfAmountIsNegative() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.valueOf(-1L));
        createDto.setAccountId(UUID_2);

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfAccountNotFound() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_2);
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.empty());

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionIfDestinationAccountNotFound() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_2);
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.of(account1));
        when(accountDao.getEntity(UUID_2)).thenReturn(Optional.empty());

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = TransactionFailedException.class)
    public void shouldThrowExceptionIfAccountHaveDifferentCurrencies() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_2);
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.of(account1));
        when(accountDao.getEntity(UUID_2)).thenReturn(Optional.of(Account.builder()
                .uuid(UUID_2)
                .currency(Currency.EUR)
                .total(TOTAL_2)
                .build()));

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = TransactionFailedException.class)
    public void shouldThrowExceptionIfAccountTotalIsLessThenTransactionAmount() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.valueOf(1000L));
        createDto.setAccountId(UUID_2);
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.of(account1));
        when(accountDao.getEntity(UUID_2)).thenReturn(Optional.of(account2));

        // when
        transferTransactionService.createTransaction(createDto);
    }

    @Test(expected = TransactionFailedException.class)
    public void shouldThrowExceptionIfTransactionNotSaved() throws DaoValidationException {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_2);
        when(transactionDao.save(any(Transaction.class))).thenThrow(DaoValidationException.class);
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.of(account1));
        when(accountDao.getEntity(UUID_2)).thenReturn(Optional.of(account2));

        // when
        transferTransactionService.createTransaction(createDto);

        // then
        verify(transactionDao, times(2)).delete(anyString());
    }

    @Test(expected = TransactionFailedException.class)
    public void shouldThrowExceptionIfAccountsNotUpdated() {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_2);
        when(accountDao.updateAccounts(anyList())).thenReturn(false);
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.of(account1));
        when(accountDao.getEntity(UUID_2)).thenReturn(Optional.of(account2));

        // when
        transferTransactionService.createTransaction(createDto);

        // then
        verify(transactionDao, times(2)).delete(anyString());
    }

    @Test
    public void shouldCreateTransaction() throws DaoValidationException {
        // given
        TransferTransactionCreateDto createDto = new TransferTransactionCreateDto();
        createDto.setDestinationAccountId(UUID_1);
        createDto.setAmount(BigDecimal.ONE);
        createDto.setAccountId(UUID_2);
        when(accountDao.updateAccounts(anyList())).thenReturn(true);
        when(accountDao.getEntity(UUID_1)).thenReturn(Optional.of(account1));
        when(accountDao.getEntity(UUID_2)).thenReturn(Optional.of(account2));
        when(transactionDao.save(any(Transaction.class))).thenAnswer(returnsFirstArg());

        // when
        TransactionDto result = transferTransactionService.createTransaction(createDto);

        // then
        verify(transactionDao, never()).delete(anyString());
        TransactionDto expected = TransactionDto.builder()
                .amount(BigDecimal.valueOf(-1L))
                .currency(CURRENCY_1)
                .type(TRANSFER_BETWEEN_ACCOUNTS)
                .build();
        assertThat(result).isEqualToIgnoringGivenFields(expected, "createdAt");
    }

}
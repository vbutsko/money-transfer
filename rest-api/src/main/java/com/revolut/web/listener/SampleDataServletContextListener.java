package com.revolut.web.listener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dao.exception.DaoValidationException;
import com.revolut.dao.model.Account;
import com.revolut.dao.model.Currency;
import com.revolut.dao.model.Transaction;
import com.revolut.dao.model.TransactionType;
import com.revolut.dao.transation.InMemoryTransactionDao;
import com.revolut.dao.transation.TransactionDao;

public class SampleDataServletContextListener implements ServletContextListener {

    private final static String INSERT_SAMPLE_DATA_PARAMETER = "insertSampleData";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            initData(servletContextEvent);
        } catch (DaoValidationException ex) {
            throw new RuntimeException("Problems during init db");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private void initData(ServletContextEvent servletContextEvent) throws DaoValidationException {
        String insertSampleData = servletContextEvent.getServletContext().getInitParameter(INSERT_SAMPLE_DATA_PARAMETER);

        if (Boolean.valueOf(insertSampleData)) {
            AccountDao accountDao = InMemoryAccountDao.getInstance();
            TransactionDao transactionDao = InMemoryTransactionDao.getInstance();
            Account account1 = accountDao.save(Account.builder()
                    .uuid("account-1")
                    .total(BigDecimal.valueOf(1000L))
                    .currency(Currency.USD)
                    .build());
            Account account2 = accountDao.save(Account.builder()
                    .uuid("account-2")
                    .total(BigDecimal.valueOf(2000L))
                    .currency(Currency.USD)
                    .build());

            transactionDao.save(Transaction.builder()
                    .uuid("transfer-1")
                    .ownerAccountId(account1.getId())
                    .otherAccountId(account2.getId())
                    .currency(Currency.USD)
                    .createdAt(LocalDateTime.now())
                    .amount(BigDecimal.valueOf(100L))
                    .type(TransactionType.TRANSFER_BETWEEN_ACCOUNTS)
                    .description("Transfer")
                    .build());
            transactionDao.save(Transaction.builder()
                    .uuid("transfer-2")
                    .ownerAccountId(account1.getId())
                    .otherAccountId(account2.getId())
                    .currency(Currency.USD)
                    .createdAt(LocalDateTime.now())
                    .amount(BigDecimal.valueOf(10L))
                    .type(TransactionType.TRANSFER_BETWEEN_ACCOUNTS)
                    .description("Transfer")
                    .build());

            transactionDao.save(Transaction.builder()
                    .uuid("transfer-3")
                    .ownerAccountId(account2.getId())
                    .otherAccountId(account1.getId())
                    .currency(Currency.USD)
                    .createdAt(LocalDateTime.now())
                    .amount(BigDecimal.valueOf(500L))
                    .type(TransactionType.TRANSFER_BETWEEN_ACCOUNTS)
                    .description("Transfer")
                    .build());
        }
    }

}

package com.revolut.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revolut.dao.account.AccountDao;
import com.revolut.dao.account.InMemoryAccountDao;
import com.revolut.dao.transation.InMemoryTransactionDao;
import com.revolut.dao.transation.TransactionDao;
import com.revolut.model.Account;
import com.revolut.model.Currency;
import com.revolut.model.Transaction;
import com.revolut.model.TransactionType;

public class SampleDataServletContextListener implements ServletContextListener {
    private final static String INSERT_SAMPLE_DATA_PARAMETER = "insertSampleData";
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String insertSampleData = servletContextEvent.getServletContext().getInitParameter(INSERT_SAMPLE_DATA_PARAMETER);

        if (Boolean.valueOf(insertSampleData)) {
            AccountDao accountDao  = InMemoryAccountDao.getInstance();
            TransactionDao transactionDao  = InMemoryTransactionDao.getInstance();
            Account account1 = accountDao.save(Account.builder()
                    .uuid("account-1")
                    .total(BigDecimal.valueOf(1000L))
                    .currency(Currency.USD)
                    .build());
            transactionDao.save(Transaction.builder()
                    .uuid("transfer-1")
                    .accountId(account1.getId())
                    .currency(Currency.USD)
                    .createdAt(LocalDateTime.now())
                    .amount(BigDecimal.valueOf(100L))
                    .type(TransactionType.TRANSFER_BETWEEN_ACCOUNTS)
                    .description("Transfer")
                    .build());
            transactionDao.save(Transaction.builder()
                    .uuid("transfer-2")
                    .accountId(account1.getId())
                    .currency(Currency.USD)
                    .createdAt(LocalDateTime.now())
                    .amount(BigDecimal.valueOf(10L))
                    .type(TransactionType.TRANSFER_BETWEEN_ACCOUNTS)
                    .description("Transfer")
                    .build());

            Account account2 = accountDao.save(Account.builder()
                    .uuid("account-2")
                    .total(BigDecimal.valueOf(2000L))
                    .currency(Currency.USD)
                    .build());
            transactionDao.save(Transaction.builder()
                    .uuid("transfer-3")
                    .accountId(account2.getId())
                    .currency(Currency.USD)
                    .createdAt(LocalDateTime.now())
                    .amount(BigDecimal.valueOf(500L))
                    .type(TransactionType.TRANSFER_BETWEEN_ACCOUNTS)
                    .description("Transfer")
                    .build());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}

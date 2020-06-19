package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.*;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import com.db.awmd.challenge.service.AccountsTransactionalService;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTransactionServiceTest {

    private final static String IdFrom = "ID-123";
    private final static String IdTo = "ID-456";
    private final static BigDecimal amount = new BigDecimal(500);
    private final String INSUFFICIENT_BALANCE = "Current balance is insufficient to perform the transaction. ";
    private final String NEGATIVE_AMOUNT_MESSAGE = "The amount to be transferred can not be negative. ";
    private final String SERVICE_EXP_MSG = " Exception occurred while servicing the transaction. ";

    @Autowired
    private AccountsTransactionalService transactionalService;

    @Autowired
    private AccountsService accountsService;

    public void addAccount(Account account, BigDecimal balance) throws Exception {

        account.setBalance(balance);
        this.accountsService.createAccount(account);
    }

    @Before // This will be invoked before every test case because we need to create accounts first.
    public void createAccounts() throws Exception
    {
        Account accountFrom = new Account(IdFrom);
        Account accountTo = new Account(IdTo);
        addAccount(accountFrom, new BigDecimal(3000));
        addAccount(accountTo, new BigDecimal(1500));
    }

    @Test
    public void transferAmountHappyPath()
    {
        boolean result = this.transactionalService.transferMoneyToOtherAccounts(IdFrom, IdTo, amount);

        // Below conditions do the verification of deducted/transferred amount.
        assertTrue(result == true);
        assertThat(this.accountsService.getAccount(IdFrom).getBalance()).isEqualTo(new BigDecimal(2500)); // 3000 - 500
        assertThat(this.accountsService.getAccount(IdTo).getBalance()).isEqualTo(new BigDecimal(2000));   // 1500 + 200
    }

    @Test
    public void transferAmountNegativeAmount() {

        try {
            this.transactionalService.transferMoneyToOtherAccounts(IdFrom, IdTo, amount.multiply(new BigDecimal(-1)) );
            fail("Should have failed when transferring negative account");
        }
        catch(TransactionServiceException exp)
        {
            assertThat(exp.getMessage()).isEqualTo(SERVICE_EXP_MSG + NEGATIVE_AMOUNT_MESSAGE);
        }
    }

    @Test
    public void transferAmountInsufficientAmount() {

        try {
            this.transactionalService.transferMoneyToOtherAccounts(IdFrom, IdTo, new BigDecimal(5000));
            fail("Should have failed when amount is insufficient in the account");
        }
        catch(TransactionServiceException exp)
        {
            assertThat(exp.getMessage()).isEqualTo(SERVICE_EXP_MSG + INSUFFICIENT_BALANCE);
        }
    }

    @After    // This will be invoked after every test case.
    public void clearAccounts()
    {
        accountsService.getAccountsRepository().clearAccounts();
    }


}

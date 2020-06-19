package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;

import java.math.BigDecimal;

public interface AccountsTransactionalRepository {

    boolean transaferMoneyToOtherAccounts(Account accountFrom, Account accountTo, BigDecimal amount);
}

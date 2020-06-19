package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.repository.AccountsTransactionalRepository;
import com.db.awmd.challenge.exception.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsTransactionalService {

  private final String SERVICE_EXP_MSG = " Exception occurred while servicing the transaction. ";
  private final String NOTIFICATION_DESCRIPTION = " Amount is transferred successfully ";

  @Getter
  private final AccountsTransactionalRepository transactionalRepository;

  @Getter
  private final AccountsRepository accountsRepository;

  //@Getter TODO: When Notification Service feature will be implemented, we can consume it.
  //private final NotificationService notificationService;

  @Autowired
  public AccountsTransactionalService(AccountsTransactionalRepository transactionalRepository, AccountsRepository accountsRepository) { //NotificationService notificationService) {
    this.transactionalRepository = transactionalRepository;
    this.accountsRepository = accountsRepository;
    //this.notificationService = notificationService;
  }

  public boolean transferMoneyToOtherAccounts(String IdFrom, String IdTo, BigDecimal amount) {

    boolean result = false;
    Account accountFrom = this.accountsRepository.getAccount(IdFrom);
    Account accountTo = this.accountsRepository.getAccount(IdTo);

    try {
      result = this.transactionalRepository.transaferMoneyToOtherAccounts(accountFrom, accountTo, amount);
    }
    catch(TransactionServiceException exp)
    {
      throw new TransactionServiceException( SERVICE_EXP_MSG + exp.getMessage());
    }
    //notificationService.notifyAboutTransfer(accountTo, NOTIFICATION_DESCRIPTION); TODO :  bean of Notification service created by COLLEAGUE.
    return result;
  }

}

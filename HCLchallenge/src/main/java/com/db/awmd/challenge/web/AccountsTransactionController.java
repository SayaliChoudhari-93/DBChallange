package com.db.awmd.challenge.web;

import com.db.awmd.challenge.exception.TransactionServiceException;
import com.db.awmd.challenge.service.AccountsTransactionalService;
import com.db.awmd.challenge.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.logging.Logger;

@RestController
@RequestMapping("/v1/transactions")
@Slf4j
public class AccountsTransactionController {

  Logger log = Logger.getLogger(AccountsTransactionController.class.getName());

  private final AccountsTransactionalService transactionalService;

  @Autowired
  public AccountsTransactionController(AccountsTransactionalService transactionalService) {
    this.transactionalService = transactionalService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> transferAmount(@RequestBody @Valid AccountTransaction accountTransaction) {

    String accountFromId = accountTransaction.getAccountFromId();
    String accountToId = accountTransaction.getAccountToId();
    BigDecimal amount = accountTransaction.getAmount();
    log.info("Requested Transaction is initiated: Transferring from : " + accountFromId  + " to account  " + accountToId + "for amount " + amount);

    try {
      this.transactionalService.transferMoneyToOtherAccounts(accountFromId, accountToId, amount);
    }
    catch (TransactionServiceException e) {
      log.warning("Transaction is failed. Could not transfer amount. " + e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

}

package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.*;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.logging.Logger;


@Repository
public class AccountsTransactionalRepositoryImpl implements AccountsTransactionalRepository {

  Logger log = Logger.getLogger(AccountsTransactionalRepositoryImpl.class.getName());

  private final String INSUFFICIENT_BALANCE = "Current balance is insufficient to perform the transaction. ";
  private final String SUCCESS = "SUCCESS";
  private final String NEGATIVE_AMOUNT_MESSAGE = "The amount to be transferred can not be negative. ";
  private final int ERROR_CODE = 409;     // This code will be mentioned in User Guide.


  /**
   *
   * The method to perform transfer of money from one account to other account.
   *
   * @param accountFrom The account from which money has to be deducted.
   * @param accountTo   The account to which money has to be deposited.
   * @param amount      The amount of transfer
   * @return            True if transaction succeeded otherwise false
   * @throws            NegativeAmountException if provided amount is negative
   * @throws            TransferMoneyException if balance in accountFrom is less than the amount
   */


  /**
   *     Note:
   *     below is alternate way of applying lock on object account
   *     accountFrom.getAccountLock().writeLock().lock();
   *     accountTo.getAccountLock().writeLock().lock();
   *     The Lock should be applied in a particular sequence.
   *     Below Operations are atomic in nature
   */

  @Override
  public boolean transaferMoneyToOtherAccounts(Account accountFrom, Account accountTo, BigDecimal amount)throws TransferMoneyException, NegativeAmountException
  {
    BigDecimal zero = new BigDecimal(0);
    if(zero.compareTo(amount) == 1)
    {
      log.warning("Transaction is failed. Could not transfer amount. " + NEGATIVE_AMOUNT_MESSAGE);
      // Actually this possibility of negative number is omitted at controller layer itself
      // But value of amount can be tampered at runtime, so it is important to handle this case.
      throw new NegativeAmountException(NEGATIVE_AMOUNT_MESSAGE);
    }

    try{                                                              // Atomicity is maintained.
      boolean stepOne = withdrawFromAccount(accountFrom, amount);
      if(stepOne == true)
        transferToAccount(accountTo, amount);
    }
    catch(InsufficientAmountException e)
    {
      log.warning("Transaction is failed. Could not transfer amount. " + e.getMessage());
      throw new TransferMoneyException(e.getMessage(), ERROR_CODE);
    }
    return true;
  }

  /**
   * Method which performs operation of withdraw of amount from the account.
   *
   * @param account             The account from which money has to be deducted.
   * @param moneyToBeDrawn      The amount of transfer
   * @return                    True if operation succeeds
   */
  boolean withdrawFromAccount(Account account, BigDecimal moneyToBeDrawn) throws InsufficientAmountException
  {
    boolean result = false;
    synchronized (account) {                                   // Lock is applied on accountFrom  : Resource1
      BigDecimal balance = account.getBalance();
      if (balance.compareTo(moneyToBeDrawn) == 0 ||  balance.compareTo(moneyToBeDrawn) == 1) {
        BigDecimal newBalance = balance.subtract(moneyToBeDrawn);
        account.setBalance(newBalance);
      }
      else
        throw new InsufficientAmountException(INSUFFICIENT_BALANCE);
    }
    log.info("Amount:" + moneyToBeDrawn  + " is deducted from the account: " + account.getAccountId());
    result =  true;
    return result;
  }

  /**
   *
   * Method which performs operation of deposit of amount to the account.
   *
   * @param account                   The account to which money has to be deposited.
   * @param moneyToBeTransferred      The amount of transfer
   * @return                          True if operation succeeds
   */
  boolean transferToAccount(Account account, BigDecimal moneyToBeTransferred)
  {
    boolean result = false;
    synchronized (account){                                     // Lock is applied on accountTo:  Resource2
      BigDecimal balance =  account.getBalance();
      BigDecimal newBalance = balance.add(moneyToBeTransferred);
      account.setBalance(newBalance);
    }
    log.info("Amount:" + moneyToBeTransferred  + " is transferred to the account: " + account.getAccountId());
    result =  true;
    return result;
  }

}

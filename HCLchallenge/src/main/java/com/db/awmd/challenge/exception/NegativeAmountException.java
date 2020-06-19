package com.db.awmd.challenge.exception;

public class NegativeAmountException extends TransactionServiceException {

  public NegativeAmountException(String message) {
    super(message);
  }
}

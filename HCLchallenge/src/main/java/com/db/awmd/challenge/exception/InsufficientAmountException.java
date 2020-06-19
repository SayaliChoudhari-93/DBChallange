package com.db.awmd.challenge.exception;

public class InsufficientAmountException extends TransactionServiceException {

  public InsufficientAmountException(String message) {
    super(message);
  }
}

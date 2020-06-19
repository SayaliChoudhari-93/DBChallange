package com.db.awmd.challenge.exception;

public class TransferMoneyException extends TransactionServiceException{

  public TransferMoneyException(String message, int errorCode)
  {
      super(message, errorCode);
  }
}

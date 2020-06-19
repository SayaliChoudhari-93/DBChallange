package com.db.awmd.challenge.exception;

public class TransactionServiceException extends RuntimeException {

    int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public TransactionServiceException(String message) {
        super(message);
    }

    public TransactionServiceException(String message, int errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }
}

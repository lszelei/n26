package com.n26.exception;

public class InvalidTransactionException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -907992038671331548L;

	public InvalidTransactionException() {
	}

	public InvalidTransactionException(Exception cause) {
		super(cause);
	}

}

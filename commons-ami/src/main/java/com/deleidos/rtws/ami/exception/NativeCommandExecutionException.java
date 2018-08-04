package com.deleidos.rtws.ami.exception;

public class NativeCommandExecutionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3998990413464528507L;

	public NativeCommandExecutionException() {
		super();
	}

	public NativeCommandExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NativeCommandExecutionException(String message) {
		super(message);
	}

	public NativeCommandExecutionException(Throwable cause) {
		super(cause);
	}

}

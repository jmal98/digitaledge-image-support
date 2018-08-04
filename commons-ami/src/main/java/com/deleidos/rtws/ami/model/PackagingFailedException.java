package com.deleidos.rtws.ami.model;

public class PackagingFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public PackagingFailedException() {
		super();
	}

	public PackagingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PackagingFailedException(String message) {
		super(message);
	}

	public PackagingFailedException(Throwable cause) {
		super(cause);
	}
}

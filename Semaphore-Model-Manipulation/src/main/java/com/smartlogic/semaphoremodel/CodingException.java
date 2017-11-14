package com.smartlogic.semaphoremodel;

public class CodingException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CodingException(String message) {
		super(message);
	}

	public CodingException(String format, Object... arguments) {
		super(String.format(format, arguments));
	}

}

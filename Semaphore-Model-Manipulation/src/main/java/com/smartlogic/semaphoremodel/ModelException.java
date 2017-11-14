package com.smartlogic.semaphoremodel;

public class ModelException extends Exception {
	private static final long serialVersionUID = 1L;

	protected ModelException(String message) {
		super(message);
	}

	protected ModelException(String format, Object... arguments) {
		super(String.format(format, arguments));
	}
}

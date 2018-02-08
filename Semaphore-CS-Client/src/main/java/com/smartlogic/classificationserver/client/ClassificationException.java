package com.smartlogic.classificationserver.client;

/**
 * General purpose exception thrown by classification server.
 * All exceptions that are thrown within the ClassificationServer client
 * are converted to this type.
 * @author Smartlogic Semaphore
 *
 */
public class ClassificationException extends Exception {
	private static final long serialVersionUID = 5268898355630890692L;

	protected ClassificationException(String message) {
		super(message);
	}
	
	public ClassificationException(String format, Object... arguments) {
		super(String.format(format, arguments));
	}
}

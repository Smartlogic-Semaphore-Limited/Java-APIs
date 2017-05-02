package com.smartlogic.classificationserver.client;


/**
 * Container for a classification record that is actually an error record.
 * @author Smartlogic Semaphore
 *
 */
public class ErrorRecord extends ClassificationRecord {

	public ErrorRecord(String[] data, AuditFormat format) {
		super(data, format);
	}

}

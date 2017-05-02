package com.smartlogic.classificationserver.client;

/**
 * Container for a classification record that is actually a success record.
 * @author Smartlogic Semaphore
 *
 */
public class SuccessRecord extends ClassificationRecord {

	public SuccessRecord(String[] data, AuditFormat format) {
		super(data, format);
	}

}

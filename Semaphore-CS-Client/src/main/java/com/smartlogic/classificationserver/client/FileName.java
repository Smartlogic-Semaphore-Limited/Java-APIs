package com.smartlogic.classificationserver.client;

/**
 * Class to represent the name of a file associated with the document sent to classification server
 * 
 * @author Smartlogic Semaphore
 *
 */
public class FileName {
	
	private String value;
	/**
	 * Create the title from the supplied data
	 * @param value The file name
	 */
	public FileName(String value) {
		this.value = value;
	}
	
	/** 
	 * The value of the file name
	 * @return the value
	 */
	protected String getValue() {
		return value;
	}
}

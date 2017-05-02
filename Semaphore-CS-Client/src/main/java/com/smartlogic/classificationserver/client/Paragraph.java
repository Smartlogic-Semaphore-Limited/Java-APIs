package com.smartlogic.classificationserver.client;

/**
 * Container for the information associated with a paragraph returned by classification server
 * @author Smartlogic Semaphore
 *
 */
public class Paragraph {

	private String field = "text";
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	private String content;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}

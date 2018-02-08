package com.smartlogic.classificationserver.client;

/**
 * Class to represent the body of the document sent to classification server
 * 
 * @author Smartlogic Semaphore
 *
 */
public class Body extends StringObject {
	protected String getParameterName() { return "body"; }
	
	private final static int MAX_LENGTH = 1000;
	
	
	private final String value;
	/**
	 * Create the body field from a string
	 * @param value The string representation of the body of the document
	 */
	public Body(String value) {
		StringBuilder sb = new StringBuilder();
		int lastPos = 0;
		int currPos = 0;
		while ((currPos = value.indexOf('\n', lastPos)) != -1) {
			sb.append(breakText(value.substring(lastPos, currPos)));
			sb.append("\n\n");
			lastPos = currPos + 1;
		}
		sb.append(breakText(value.substring(lastPos)));
		
		this.value = sb.toString();
	}
	
	private String breakText(String input) {
		StringBuffer sb = new StringBuffer(); 
		String current = input;
		while (current.length() > MAX_LENGTH) {
			int endPos = MAX_LENGTH;
			while ((!Character.isWhitespace(current.charAt(endPos))) && (endPos > 0)) endPos--;
			if (endPos == 0) endPos = MAX_LENGTH; // No white space in first MAX_LENGTH characters, we cannot break at a whitespace
			sb.append(current.substring(0,endPos).trim());
			sb.append("\n\n");
			current = current.substring(endPos);
		}
		sb.append(current.trim());
		return sb.toString();
	}
	
	protected String getValue() {
		return value;
	}
}


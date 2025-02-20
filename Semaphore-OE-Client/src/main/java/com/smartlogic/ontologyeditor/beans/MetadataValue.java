package com.smartlogic.ontologyeditor.beans;

public class MetadataValue {

	private final String value;
	private final String languageCode;
	public String getValue() {
		return value;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	
	public MetadataValue(String languageCode, String value) {
		this.value = value;
		this.languageCode = languageCode;
	}
	
	@Override
	public String toString() {

		return (languageCode == null || languageCode.isEmpty()) ?
				String.format("\"%s\"", value) :
				String.format("\"%s\"@%s", value, languageCode);
	}
}

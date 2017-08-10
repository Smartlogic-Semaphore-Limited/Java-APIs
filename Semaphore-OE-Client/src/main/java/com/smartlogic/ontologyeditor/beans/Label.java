package com.smartlogic.ontologyeditor.beans;

public class Label {

	private final String uri;
	public Label(String uri, String languageCode, String value) {
		this.uri = uri;
		this.languageCode = languageCode;
		this.value = value;
	}

	public Label(String languageCode, String value) {
		this.uri = null;
		this.languageCode = languageCode;
		this.value = value;
	}

	public String getUri() {
		return uri;
	}

	private String languageCode;
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguage(String languageCode) {
		this.languageCode = languageCode;
	}

	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.value);
		if (this.languageCode != null) stringBuilder.append("@").append(this.languageCode);
		if (this.uri != null) stringBuilder.append(" <").append(this.uri).append(">");
		return stringBuilder.toString();
	}

}

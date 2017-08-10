package com.smartlogic.ontologyeditor.beans;

public class Identifier {

	private final String uri;
	public String getUri() {
		return uri;
	}

	private final String value;
	public String getValue() {
		return value;
	}

	public Identifier(String uri, String value) {
		this.uri = uri;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("%s [%s]", value, uri);
	}
}

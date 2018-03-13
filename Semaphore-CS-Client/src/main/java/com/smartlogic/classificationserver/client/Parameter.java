package com.smartlogic.classificationserver.client;

public class Parameter {
	private String name;
	private String value;
	private String translation;

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getTranslation() {
		return translation;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Parameter [name=" + name + ", value=" + value + ", translation=" + translation + "]";
	}
	
}

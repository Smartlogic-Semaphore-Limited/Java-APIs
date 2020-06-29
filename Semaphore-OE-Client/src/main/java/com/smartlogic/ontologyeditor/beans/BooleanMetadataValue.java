package com.smartlogic.ontologyeditor.beans;

public class BooleanMetadataValue {

	private final boolean value;

	public boolean getValue() {
		return value;
	}
	
	public BooleanMetadataValue(boolean value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("%s", Boolean.toString(value));
	}
}

package com.smartlogic.ontologyeditor;

public class OEFilter {

	private String conceptClass;
	public String getConceptClass() {
		return conceptClass;
	}
	public void setConceptClass(String conceptClass) {
		this.conceptClass = conceptClass;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.getClass().getSimpleName());
		stringBuilder.append(" [");
		stringBuilder.append("conceptClass = '").append(conceptClass).append("'");
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}

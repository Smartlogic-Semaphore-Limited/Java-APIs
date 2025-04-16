package com.smartlogic.ontologyeditor;

public class OEFilter {

	private String conceptClass;

	/**
	 * Gets the concept class filter. Example: skos:Concept
	 * @return the concept class filter string
	 */
	public String getConceptClass() {
		return conceptClass;
	}

	/**
	 * Sets the concept class filter string. For example: skos:Concept
	 * @param conceptClass the concept class filter string
	 */
	public void setConceptClass(String conceptClass) {
		this.conceptClass = conceptClass;
	}

	private String labelPrefix;

	/**
	 * Gets the concept label prefix search string. For example "Acti"
	 * The prefix search string must match, case-insensitive, the beginning of a preferred or alternative label
	 * of a concept in the graph for there to be a match.
	 * For example "My" will match any concept that has any label
	 * that starts with the string "[Mm][Yy]"
	 * @return the label prefix search string
	 */
	public String getLabelPrefix() {
		return labelPrefix;
	}

	/**
	 * Sets the concept label prefix search string
	 * @param labelPrefix the concept label prefix search string.
	 */
	public void setLabelPrefix(String labelPrefix) {
		this.labelPrefix = labelPrefix;
	}

	private String labelPrefixLangCode;

	/**
	 * Gets the concept label prefix search language code
	 * @return the concept label prefix label language code
	 */
	public String getLabelPrefixLangCode() {
		return labelPrefixLangCode;
	}

	/**
	 * Sets the label prefix search string language code. For example: "en"
	 * @param labelPrefixLangCode the concept label prefix search language code.
	 */
	public void setLabelPrefixLangCode(String labelPrefixLangCode) {
		this.labelPrefixLangCode = labelPrefixLangCode;
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

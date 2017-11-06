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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		
		if (getClass() != obj.getClass()) return false;
		
		Label other = (Label) obj;
		if (languageCode == null) {
			if (other.languageCode != null)
				return false;
		} else if (!languageCode.equals(other.languageCode))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}

package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonObject;

public class Model {

	String defaultNamespace = "http://example.org/api-test#";
	public Model(JsonObject jsonObject) {
		label = new Label("", jsonObject.get("meta:displayName").getAsObject().get("@value").getAsString().value());
		uri = jsonObject.get("meta:graphUri").getAsObject().get("@id").getAsString().value();
		comment = null;
	}
	
	public Model(String uri, Label label, String comment) {
		this.uri = uri;
		this.label = label;
		this.comment = comment;
	}

	public String getDefaultNamespace() {
		return defaultNamespace;
	}
	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}
	private final String comment;
	public String getComment() {
		return comment;
	}

	private final Label label;
	public Label getLabel() {
		return label;
	}
	
	private String uri;
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUri() {
		return uri;
	}

	@Override
	public int hashCode() {
		if (uri != null) return uri.hashCode();
		
		return label.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		
		if (getClass() != obj.getClass()) return false;
		
		Model other = (Model) obj;

		if (uri == null) {
			if (other.uri != null)
				return false;
		} else 
			return uri.equals(other.uri);

		return label.equals(other.label);
	}

	@Override
	public String toString() {
		return "Model [label=" + label + ", uri=" + uri + "]";
	}

	
}

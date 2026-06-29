// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class Model {

	String defaultNamespace = "http://example.org/api-test#";
	public Model(JsonObject jsonObject) {
		JsonValue displayNameValue = jsonObject.get("meta:displayName");
		if (displayNameValue != null) {
			JsonValue valueValue = displayNameValue.getAsObject().get("@value");
			if (valueValue != null) {
				label = new Label("", jsonObject.get("meta:displayName").getAsObject().get("@value").getAsString().value());
			} else {
				label = new Label("", "No display name found");
			}
		} else {
			label = new Label("", "No display name found");
		}
		uri = jsonObject.get("meta:graphUri").getAsObject().get("@id").getAsString().value();
		comment = null;
		languages = new ArrayList<>();
		jsonObject.get("dcterms:language").getAsArray().forEach(jsonValue -> {
			languages.add(new ModelLanguage(jsonValue.getAsObject()));
		});
	}

	public Model(String uri, Label label, String comment) {
		this(uri, label, comment, new ArrayList<>());
	}
	
	public Model(String uri, Label label, String comment, List<ModelLanguage> languages) {
		this.uri = uri;
		this.label = label;
		this.comment = comment;
        this.languages = languages;
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

	private final List<ModelLanguage> languages;

	public List<ModelLanguage> getLanguages() {
		return languages;
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
		} else {
			if(!uri.equals(other.uri)) {
				return false;
			}
		}
		if(label != null ) {
             if(!label.equals(other.label)) {
				 return false;
			 }
		}
		if(comment != null) {
			return comment.equals(other.comment);
		} else {
			if (other.comment != null)
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Model [label=" + label + ", uri=" + uri + "]";
	}

	
}

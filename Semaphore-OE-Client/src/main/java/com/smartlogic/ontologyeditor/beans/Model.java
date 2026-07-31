// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class Model {

	String defaultNamespace;
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
		comment = extractFirstStringValue(jsonObject, "rdfs:comment");
		String parsedDefaultNamespace = extractFirstStringValue(jsonObject, "swa:defaultNamespace");
		if (parsedDefaultNamespace != null) {
			defaultNamespace = parsedDefaultNamespace;
		}
		languages = new ArrayList<>();
		JsonValue languagesValue = jsonObject.get("dcterms:language");
		if (languagesValue != null) {
			languagesValue.getAsArray().forEach(jsonValue -> languages.add(new ModelLanguage(jsonValue.getAsObject())));
		}
	}

	/**
	 * Extracts the first plain string value of a (possibly single- or multi-valued) JSON-LD
	 * property, e.g. {@code {"@value": "..."}} or {@code {"@id": "..."}}, or an array of such
	 * objects. Returns {@code null} if the property is absent, empty, or has neither an
	 * {@code @value} nor an {@code @id}.
	 */
	private static String extractFirstStringValue(JsonObject jsonObject, String propertyUri) {
		JsonValue propertyValue = jsonObject.get(propertyUri);
		if (propertyValue == null) {
			return null;
		}
		JsonValue firstValue = propertyValue;
		if (propertyValue.isArray()) {
			if (propertyValue.getAsArray().size() == 0) {
				return null;
			}
			firstValue = propertyValue.getAsArray().get(0);
		}
		if (firstValue.isObject() && firstValue.getAsObject().get("@value") != null) {
			return firstValue.getAsObject().get("@value").getAsString().value();
		}
		if (firstValue.isObject() && firstValue.getAsObject().get("@id") != null) {
			return firstValue.getAsObject().get("@id").getAsString().value();
		}
		if (firstValue.isString()) {
			return firstValue.getAsString().value();
		}
		return null;
	}

	public Model(String uri, Label label, String comment) {
		this(uri, label, comment, new ArrayList<>());
	}

	/**
	 * Create a model with explicit URI, label, comment, and default namespace. Use this
	 * constructor (rather than {@link #Model(String, Label, String)}) when the model is going
	 * to be passed to {@link com.smartlogic.ontologyeditor.OEClientReadWrite#createModel(Model)},
	 * which requires a non-blank default namespace.
	 *
	 * @param uri the model URI
	 * @param label the model display label
	 * @param comment the model comment
	 * @param defaultNamespace the model's default namespace, e.g. {@code "http://example.com/my-model#"}
	 */
	public Model(String uri, Label label, String comment, String defaultNamespace) {
		this(uri, label, comment, new ArrayList<>());
		this.defaultNamespace = defaultNamespace;
	}

	/**
	 * Create a model with explicit URI, label, comment, and languages.
	 *
	 * @param uri the model URI
	 * @param label the model display label
	 * @param comment the model comment
	 * @param languages the languages associated with the model
	 */
	public Model(String uri, Label label, String comment, List<ModelLanguage> languages) {
		this.uri = uri;
		this.label = label;
		this.comment = comment;
        this.languages = languages == null ? new ArrayList<>() : languages;
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

	/**
	 * Get the languages associated with the model.
	 *
	 * @return the model languages, never {@code null}
	 */
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
		if (label != null) {
             if(!label.equals(other.label)) {
				 return false;
			 }
		} else if (other.label != null) {
			return false;
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

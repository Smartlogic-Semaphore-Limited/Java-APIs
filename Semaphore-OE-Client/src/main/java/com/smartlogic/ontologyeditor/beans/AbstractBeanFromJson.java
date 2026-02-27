package com.smartlogic.ontologyeditor.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

import com.smartlogic.ontologyeditor.OEClientReadOnly;

public abstract class AbstractBeanFromJson {

	@JsonIgnore
	protected OEClientReadOnly oeClient;
	protected String uri;
	public String getUri() {
		return uri;
	}

	protected String getAsString(JsonObject jsonObject, String property) {
		JsonValue jsonValue = jsonObject.get(property);
		return jsonValue == null ? null : jsonValue.getAsString().value();
	}

	protected JsonArray getAsArray(JsonObject jsonObject, String property) {
		JsonValue jsonValue = jsonObject.get(property);
		return jsonValue == null ? new JsonArray() : jsonValue.getAsArray();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractBeanFromJson that = (AbstractBeanFromJson) o;

        return uri.equals(that.uri);
    }

	@Override
	public int hashCode() {
		return uri.hashCode();
	}
}

package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

import com.smartlogic.ontologyeditor.OEClientReadOnly;

public abstract class AbstractBeanFromJson {

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


}

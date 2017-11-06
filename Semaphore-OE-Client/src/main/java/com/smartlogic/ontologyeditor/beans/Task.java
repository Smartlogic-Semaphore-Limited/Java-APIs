package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonObject;

public class Task {

	private final Label label;
	private final String id;
	private final String graphUri;
	public Task(Label label) {
		this.label = label;
		this.id = null;
		this.graphUri = null;
	}

	public Task(JsonObject jsonObject) {
		label = new Label(jsonObject.get("meta:displayName").getAsObject().get("@value").toString(), "");
		id = jsonObject.get("@id").toString();
		graphUri = jsonObject.get("meta:graphUri").getAsObject().get("@id").toString();
	}

	public Task(Label label, String id, String graphUri) {
		this.label = label;
		this.id = id;
		this.graphUri = graphUri;
	}

	public String getId() {
		return id;
	}

	public String getGraphUri() {
		return graphUri;
	}

	public Label getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return String.format("Task: '%s' [%s] {%s}",  label, id, graphUri);
	}
	
}

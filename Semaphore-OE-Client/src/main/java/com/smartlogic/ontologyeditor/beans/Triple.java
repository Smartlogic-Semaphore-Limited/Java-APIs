package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JsonObject;

public class Triple {

	private final String subject;
	public String getSubject() {
		return subject;
	}

	private final String predicate;
	public String getPredicate() {
		return predicate;
	}


	private final String objectId;
	public String getObjectId() {
		return objectId;
	}

	private final String objectValue;
	public String getObjectValue() {
		return objectValue;
	}

	private final String objectLanguage;
	public String getObjectLanguage() {
		return objectLanguage;
	}


	public Triple(JsonObject jsonObject) {
		this.subject = jsonObject.get("teamwork:subject").getAsArray().get(0).getAsObject().get("@id").getAsString().value();
		this.predicate = jsonObject.get("teamwork:predicate").getAsArray().get(0).getAsObject().get("@id").getAsString().value();
		
		JsonObject object = jsonObject.get("teamwork:object").getAsArray().get(0).getAsObject();
		if (object.get("@id") != null) {
			this.objectId = object.get("@id").getAsString().value();
			this.objectLanguage = null;
			this.objectValue = null;
		} else {
			this.objectId = null;
			this.objectValue = object.get("@value").getAsString().value();
			if (object.get("@language") != null) {
				this.objectLanguage = object.get("@language").getAsString().value();
			} else {
				this.objectLanguage = null;				
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("S: ").append(this.subject).append(" ");
		stringBuilder.append("P: ").append(this.predicate).append(" ");
		if (this.objectId != null) {
			stringBuilder.append("O: ").append(this.objectId).append(" ");			
		} else {
			stringBuilder.append("O: \"").append(this.objectValue).append("\"");
			if (this.objectLanguage != null) {
				stringBuilder.append("@\"").append(this.objectLanguage).append("\"");
			}
		}
		return stringBuilder.toString();
	}
}

package com.smartlogic.ontologyeditor.beans;

import java.util.Collection;
import java.util.HashSet;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientReadOnly;

public class RelationshipType extends AbstractBeanFromJson {
	protected final static Logger logger = LoggerFactory.getLogger(RelationshipType.class);

	private Collection<Label> labels;
	public Collection<Label> getLabels() {
		return labels;
	}

	private Collection<String> parentPropertyUris;
	public Collection<String> getParentPropertyUris() {
		return parentPropertyUris;
	}

	public RelationshipType(OEClientReadOnly oeClient, JsonObject jsonObject) {
		logger.debug("RelationshipType - entry: {}", jsonObject);
		this.oeClient = oeClient;
		this.uri = getAsString(jsonObject, "@id");

		labels = new HashSet<Label>();
		JsonArray jsonLabelValues = getAsArray(jsonObject, "rdfs:label");
		for (int i = 0; i < jsonLabelValues.size(); i++) {
			JsonObject jsonLabel = jsonLabelValues.get(i).getAsObject();
			String nameLabelValue = getAsString(jsonLabel, "@value");
			String nameLabelLangCode = getAsString(jsonLabel, "@language");

			labels.add(new Label(nameLabelLangCode, nameLabelValue));
		}

		parentPropertyUris = new HashSet<String>();
		JsonArray parentProperties = getAsArray(jsonObject, "rdfs:subPropertyOf");
		for (int i = 0; i < parentProperties.size(); i++) {
			JsonObject parentProperty = parentProperties.get(i).getAsObject();
			parentPropertyUris.add(getAsString(parentProperty, "@id"));
		}

		logger.info("RelationshipType - exit: {} {}", this.uri, this.labels);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Relationship Type:");
		stringBuilder.append(this.uri).append(":");
		for (Label label: labels) stringBuilder.append(" ").append(label);
		stringBuilder.append(" [");
		String sep = "";
		for (String parentPropertyUri: parentPropertyUris) {
			stringBuilder.append(sep).append(parentPropertyUri);
			sep = ", ";
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}

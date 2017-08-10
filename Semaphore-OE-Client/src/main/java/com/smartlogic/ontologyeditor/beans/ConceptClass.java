package com.smartlogic.ontologyeditor.beans;

import java.util.Collection;
import java.util.HashSet;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientReadOnly;

public class ConceptClass extends AbstractBeanFromJson {
	protected final static Logger logger = LoggerFactory.getLogger(ConceptClass.class);

	private Collection<Label> labels;
	public Collection<Label> getLabels() {
		return labels;
	}

	private Collection<String> parentClassUris;
	public Collection<String> getParentClassUris() {
		return parentClassUris;
	}

	public ConceptClass(OEClientReadOnly oeClient, JsonObject jsonObject) {
		logger.debug("ConceptClass - entry: {}", jsonObject);
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

		parentClassUris = new HashSet<String>();
		JsonArray parentClasses = getAsArray(jsonObject, "rdfs:subClassOf");
		for (int i = 0; i < parentClasses.size(); i++) {
			JsonObject parentClass = parentClasses.get(i).getAsObject();
			parentClassUris.add(getAsString(parentClass, "@id"));
		}

		logger.info("ConceptClass - exit: {} {}", this.uri, this.labels);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Concept Class:");
		stringBuilder.append(this.uri).append(":");
		for (Label label: labels) stringBuilder.append(" ").append(label);
		stringBuilder.append(" [");
		String sep = "";
		for (String parentClassUri: parentClassUris) {
			stringBuilder.append(sep).append(parentClassUri);
			sep = ", ";
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

}

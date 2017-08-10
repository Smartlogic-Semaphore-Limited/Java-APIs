package com.smartlogic.ontologyeditor.beans;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientReadOnly;

public class ConceptScheme extends AbstractBeanFromJson {
	protected final static Logger logger = LoggerFactory.getLogger(ConceptScheme.class);

	private Collection<String> types = new HashSet<String>();
	private Collection<Label> prefLabels = new HashSet<Label>();

	public ConceptScheme(OEClientReadOnly oeClient, JsonObject jsonObject) {
		logger.debug("Concept - entry: {}", jsonObject);
		this.uri = jsonObject.get("@id").getAsString().value();

		JsonValue jsonValue = jsonObject.get("@type");
		if (jsonValue != null) {
			JsonArray jsonTypes = jsonValue.getAsArray();
			for (int i = 0; i < jsonTypes.size(); i++) {
				this.types.add(jsonTypes.get(i).getAsString().value());
			}
		}

		JsonArray jsonPrefLabels = jsonObject.get("skosxl:prefLabel").getAsArray();
		for (int i = 0; i < jsonPrefLabels.size(); i++) {
			JsonObject jsonPrefLabel = jsonPrefLabels.get(i).getAsObject();

			String prefLabelUri = jsonPrefLabel.get("@id").getAsString().value();
			JsonObject jsonLiteral = jsonPrefLabel.get("meta:displayName").getAsObject();
			String prefLabelValue = jsonLiteral.get("@value").getAsString().value();
			String prefLabelLangCode = jsonLiteral.get("@language").getAsString().value();

			prefLabels.add(new Label(prefLabelUri, prefLabelLangCode, prefLabelValue));
		}
		logger.info("Concept - exit: {}", this.uri);

	}

	public ConceptScheme(OEClientReadOnly oeClient, String uri, List<Label> labelList) {
		this.oeClient = oeClient;
		this.uri = uri;
		prefLabels.addAll(labelList);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("Concept:");
		stringBuilder.append(this.uri).append(" [");
		String sep = "";
		for (String type: types) {
			stringBuilder.append(sep).append(type);
			sep = ", ";
		}
		stringBuilder.append("] ");
		for (Label prefLabel: prefLabels) {
			stringBuilder.append(" \"").append(prefLabel.toString()).append("\"");
		}
		return stringBuilder.toString();
	}


	public Collection<Label> getPrefLabels() {
		return prefLabels;
	}
}

package com.smartlogic.ontologyeditor.beans;

import com.smartlogic.ontologyeditor.OEClientReadOnly;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;

public class MetadataType extends AbstractBeanFromJson {
	protected final static Logger logger = LoggerFactory.getLogger(MetadataType.class);

	private Collection<Label> labels;
	public Collection<Label> getLabels() {
		return labels;
	}

	public Collection<String> getDomains() {
		return domains;
	}

	private Collection<String> domains;

	public Collection<String> getRanges() {
		return ranges;
	}

	private Collection<String> ranges;

	private Collection<String> parentPropertyUris;
	public Collection<String> getParentPropertyUris() {
		return parentPropertyUris;
	}

	public MetadataType(OEClientReadOnly oeClient, JsonObject jsonObject) {
		logger.debug("MetadataType - entry");
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

		ranges = new HashSet<String>();
		JsonArray rangeValues = getAsArray(jsonObject, "rdfs:range");
		for (int i = 0; i < rangeValues.size(); i++) {
			JsonObject rangeValue = rangeValues.get(i).getAsObject();
			ranges.add(getAsString(rangeValue, "@id"));
		}

		domains = new HashSet<String>();
		JsonArray domainValues = getAsArray(jsonObject, "rdfs:domain");
		for (int i = 0; i < domainValues.size(); i++) {
			JsonObject domainValue = domainValues.get(i).getAsObject();
			domains.add(getAsString(domainValue, "@id"));
		}

		logger.info("RelationshipType - exit: {} {}", this.uri, this.labels);
	}

	@Override
	public String toString() {
		return "MetadataType{" +
				"labels=" + labels +
				", domains=" + domains +
				", ranges=" + ranges +
				", parentPropertyUris=" + parentPropertyUris +
				'}';
	}


}

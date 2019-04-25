package com.smartlogic.ontologyeditor.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientException;
import com.smartlogic.ontologyeditor.OEClientReadOnly;

public class Concept extends AbstractBeanFromJson {
	protected final static Logger logger = LoggerFactory.getLogger(Concept.class);

	private static final String GUID_RELATIONSHIP_URI = "sem:guid";

	private Collection<String> types = new HashSet<String>();
	private Collection<Label> prefLabels = new HashSet<Label>();
	private Map<String, Map<String, Label>> prefLabelsByLanguageAndValue = new HashMap<String, Map<String, Label>>();

	private Map<String, Collection<String>> relatedConceptUrisByRelationship = new HashMap<String, Collection<String>>();
	private Map<String, Collection<MetadataValue>> metadataValuesByMetadataTypeUri = new HashMap<String, Collection<MetadataValue>>();


	public Concept(OEClientReadOnly oeClient, JsonObject jsonObject) {
		logger.debug("Concept - entry: {}", jsonObject);
		this.uri = getAsString(jsonObject, "@id");

		JsonValue jsonValue = jsonObject.get("@type");
		if (jsonValue != null) {
			JsonArray jsonTypes = jsonValue.getAsArray();
			for (int i = 0; i < jsonTypes.size(); i++) {
				this.types.add(jsonTypes.get(i).getAsString().value());
			}
		}

		JsonValue jsonGuidValue = jsonObject.get(GUID_RELATIONSHIP_URI);
		if (jsonGuidValue != null) {
			JsonArray guidArray = getAsArray(jsonObject, GUID_RELATIONSHIP_URI );
			if (guidArray.size() > 0) {
				String guidVal = guidArray.get(0).getAsObject().get("@value").getAsString().value();
				Identifier guidIdentifier = new Identifier(GUID_RELATIONSHIP_URI, guidVal);
				addIdentifier(guidIdentifier);
			}
		}

		JsonArray jsonPrefLabels = getAsArray(jsonObject, "skosxl:prefLabel");
		if (jsonPrefLabels != null) {
			for (int i = 0; i < jsonPrefLabels.size(); i++) {
				JsonObject jsonPrefLabel = jsonPrefLabels.get(i).getAsObject();

				String prefLabelUri = getAsString(jsonPrefLabel, "@id");
				JsonArray jsonLiteralForms = getAsArray(jsonPrefLabel, "skosxl:literalForm");
				if (jsonLiteralForms != null) {
					for (int j = 0; j < jsonLiteralForms.size(); j++) {
						JsonObject jsonLiteralForm = jsonLiteralForms.get(j).getAsObject();
						String prefLabelValue = getAsString(jsonLiteralForm, "@value");
						String prefLabelLangCode = getAsString(jsonLiteralForm, "@language");

						Label label = new Label(prefLabelUri, prefLabelLangCode, prefLabelValue);
						prefLabels.add(label);
						addByLanguageAndValue(prefLabelsByLanguageAndValue, label.getLanguageCode(), label.getValue(), label);
					}
				}
			}
		}
		logger.info("Concept - exit: {}", this.uri);
	}

	public Collection<String> getBroaderConceptUris() throws OEClientException {
		return getRelatedConceptUris("skos:broader");
	}

	public Collection<String> getNarrowerConceptUris() throws OEClientException {
		return getRelatedConceptUris("skos:narrower");
	}

	public Collection<String> getRelatedConceptUris(String relationhipUri) throws OEClientException {
		Collection<String> relatedConceptURIs = relatedConceptUrisByRelationship.get(relationhipUri);
		if (relatedConceptURIs == null) {
			oeClient.populateRelatedConceptUris(relationhipUri, this);
			relatedConceptURIs = relatedConceptUrisByRelationship.get(relationhipUri);
		}
		return relatedConceptURIs;
	}

	public void populateRelatedConceptUris(String relationhipUri, JsonObject jsonObject) {
		Collection<String> relatedConceptURIs = new HashSet<String>();
		JsonArray jsonRelateds = getAsArray(jsonObject, relationhipUri);
		if (jsonRelateds != null) {
			for (int i = 0; i < jsonRelateds.size(); i++) {
				JsonObject jsonNarrower = jsonRelateds.get(i).getAsObject();
				relatedConceptURIs.add(getAsString(jsonNarrower, "@id"));
			}
		}
		relatedConceptUrisByRelationship.put(relationhipUri, relatedConceptURIs);
	}

	public Collection<MetadataValue> getMetadata(String metadataTypeUri) throws OEClientException {
		Collection<MetadataValue> metadata = metadataValuesByMetadataTypeUri.get(metadataTypeUri);
		if (metadataTypeUri == null) {
			oeClient.populateMetadata(metadataTypeUri, this);
			metadata = metadataValuesByMetadataTypeUri.get(metadataTypeUri);
		}
		return metadata;
		
	}
	public void populateMetadata(String metadataTypeUri, JsonObject jsonObject) {
		Collection<MetadataValue> metadataValues = new HashSet<MetadataValue>();
		JsonArray jsonRelateds = getAsArray(jsonObject, metadataTypeUri);
		if (jsonRelateds != null) {
			for (int i = 0; i < jsonRelateds.size(); i++) {
				JsonObject jsonMetadata = jsonRelateds.get(i).getAsObject();
				metadataValues.add(new MetadataValue(getAsString(jsonMetadata, "@language"), getAsString(jsonMetadata, "@value")));
			}
		}
		metadataValuesByMetadataTypeUri.put(metadataTypeUri, metadataValues);
	}

	public Label getPrefLabelByLanguageAndValue(String languageCode, String value) {
		return getByLanguageAndValue(prefLabelsByLanguageAndValue, languageCode, value);
	}

	private <T> T getByLanguageAndValue(Map<String, Map<String, T>> mapByLanguageAndName, String languageCode, String value) {
		Map<String, T> mapByValue = mapByLanguageAndName.get(languageCode);
		if (mapByValue == null) return null;
		return mapByValue.get(value);

	}

	private <T> void addByLanguageAndValue(Map<String, Map<String, T>> mapByLanguageAndValue, String languageCode, String name, T t) {
		Map<String, T> mapByValue = mapByLanguageAndValue.get(languageCode);
		if (mapByValue == null) {
			mapByValue = new HashMap<String, T>();
			mapByLanguageAndValue.put(languageCode, mapByValue);
		}
		mapByValue.put(name, t);
	}

	public Concept(OEClientReadOnly oeClient, String uri, List<Label> labelList) {
		this.oeClient = oeClient;
		this.uri = uri;
		prefLabels.addAll(labelList);
	}

	private final Map<String, Identifier> identifiers = new HashMap<String, Identifier>();
	public void addIdentifier(Identifier identifier) {
		identifiers.put(identifier.getUri(), identifier);
	}
	public void setGuid(String guid) {
		identifiers.put(GUID_RELATIONSHIP_URI, new Identifier(GUID_RELATIONSHIP_URI, guid));
	}
	public String getGuid() {
		Identifier guidIdentifier = identifiers.get(GUID_RELATIONSHIP_URI);
		if (guidIdentifier == null) return null;
		else return guidIdentifier.getValue();
	}
	public Identifier getIdentifier(String relationshipUri) {
		return identifiers.get(relationshipUri);
	}
	public Collection<Identifier> getIdentifiers() {
		return identifiers.values();
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
		stringBuilder.append("\nPref Labels: ");
		for (Label prefLabel: prefLabels) {
			stringBuilder.append(" \"").append(prefLabel.toString()).append("\"");
		}

		for (Map.Entry<String, Collection<String>> entry: relatedConceptUrisByRelationship.entrySet()) {
			stringBuilder.append("\n").append(entry.getKey()).append(": ");
			for (String relatedUri: entry.getValue()) {
				stringBuilder.append(" <").append(relatedUri).append(">");
			}
		}
		return stringBuilder.toString();
	}

	public Collection<Label> getPrefLabels() {
		return prefLabels;
	}

}

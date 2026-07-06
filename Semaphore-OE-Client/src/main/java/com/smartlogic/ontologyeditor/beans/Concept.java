// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.jena.atlas.json.JSON;
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

  private final Collection<String> types = new HashSet<>();
  private Collection<Label> prefLabels = new HashSet<>();
  private Map<String, Collection<Label>> altLabelsByUri = new HashMap<>();
  private Map<String, Map<String, Label>> prefLabelsByLanguageAndValue = new HashMap<>();

  private Map<String, Collection<String>> relatedConceptUrisByRelationship = new HashMap<>();
  private Map<String, Collection<MetadataValue>> metadataValuesByMetadataTypeUri = new HashMap<>();
  private Map<String, BooleanMetadataValue> booleanMetadataValuesByMetadataTypeUri =
      new HashMap<>();

  @JsonIgnore
  private final JsonObject jsonObject;

  /**
   * Create a concept for outbound use when no source JSON is available.
   *
   * @param oeClient the OE client to use for operations
   * @param prefLabels the preferred labels to assign to the concept
   * @param altLabels unused legacy parameter for alternative labels
   * @param relatedConceptUrisByRelationship relationship targets keyed by relationship URI
   */
  public Concept(OEClientReadOnly oeClient, List<Label> prefLabels, List<Label> altLabels, Map<String, Collection<String>> relatedConceptUrisByRelationship) {
    this.oeClient = oeClient;
    this.prefLabels = prefLabels;
    this.relatedConceptUrisByRelationship = relatedConceptUrisByRelationship != null ? relatedConceptUrisByRelationship : new HashMap<>();
    this.jsonObject = null;

  }

  public Concept(OEClientReadOnly oeClient, JsonObject jsonObject) {
    logger.debug("Concept - entry: {}", jsonObject);
    this.jsonObject = jsonObject;
    this.uri = getAsString(jsonObject, "@id");
    this.oeClient = oeClient;

    JsonValue jsonValue = jsonObject.get("@type");
    if (jsonValue != null) {
      JsonArray jsonTypes = jsonValue.getAsArray();
      for (JsonValue jsonType : jsonTypes) {
        this.types.add(jsonType.getAsString().value());
      }
    }

    JsonValue jsonGuidValue = jsonObject.get(GUID_RELATIONSHIP_URI);
    if (jsonGuidValue != null) {
      JsonArray guidArray = getAsArray(jsonObject, GUID_RELATIONSHIP_URI);
      if (guidArray.size() > 0) {
        String guidVal = guidArray.get(0).getAsObject().get("@value").getAsString().value();
        Identifier guidIdentifier = new Identifier(GUID_RELATIONSHIP_URI, guidVal);
        addIdentifier(guidIdentifier);
      }
    }

    JsonArray jsonPrefLabels = getAsArray(jsonObject, "skosxl:prefLabel");
    if (jsonPrefLabels != null) {
      for (JsonValue jsonPrefLabel2 : jsonPrefLabels) {
        JsonObject jsonPrefLabel = jsonPrefLabel2.getAsObject();

        String prefLabelUri = getAsString(jsonPrefLabel, "@id");
        JsonArray jsonLiteralForms = getAsArray(jsonPrefLabel, "skosxl:literalForm");
        if (jsonLiteralForms != null) {
          for (JsonValue jsonLiteralForm2 : jsonLiteralForms) {
            JsonObject jsonLiteralForm = jsonLiteralForm2.getAsObject();
            String prefLabelValue = getAsString(jsonLiteralForm, "@value");
            String prefLabelLangCode = getAsString(jsonLiteralForm, "@language");

            Label label = new Label(prefLabelUri, prefLabelLangCode, prefLabelValue);
            prefLabels.add(label);
            addByLanguageAndValue(prefLabelsByLanguageAndValue, label.getLanguageCode(),
                label.getValue(), label);
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

  public void populateRelatedConceptUris(String relationhipUri, JsonValue jsonValue) {
    Collection<String> relatedConceptURIs = new HashSet<>();
    JsonArray jsonRelateds = jsonValue.getAsArray();
    if (jsonRelateds != null) {
      for (JsonValue jsonRelated : jsonRelateds) {
        JsonObject jsonNarrower = jsonRelated.getAsObject();
        relatedConceptURIs.add(getAsString(jsonNarrower, "@id"));
      }
    }
    relatedConceptUrisByRelationship.put(relationhipUri, relatedConceptURIs);
  }

  public Collection<MetadataValue> getMetadata(String metadataTypeUri) throws OEClientException {
    Collection<MetadataValue> metadata = metadataValuesByMetadataTypeUri.get(metadataTypeUri);
    return metadata;

  }

  public BooleanMetadataValue getBooleanMetadata(String metadataTypeUri) throws OEClientException {
    BooleanMetadataValue metadata = booleanMetadataValuesByMetadataTypeUri.get(metadataTypeUri);
    return metadata;

  }

  public void populateMetadata(String metadataTypeUri, JsonObject jsonObject) {
    Collection<MetadataValue> metadataValues = new HashSet<>();
    JsonArray jsonValues = getAsArray(jsonObject, metadataTypeUri);
    if (jsonValues != null) {
      for (JsonValue jsonValue : jsonValues) {
        if(jsonValue.isObject()) {
          JsonObject jsonMetadata = jsonValue.getAsObject();
          metadataValues.add(new MetadataValue(getAsString(jsonMetadata, "@language"),
              getAsString(jsonMetadata, "@value")));
        } else {
          String rawVal = jsonValue.isString() ? jsonValue.getAsString().value() : jsonValue.toString();
          metadataValues.add(new MetadataValue("", rawVal));
        }
      }
    }
    metadataValuesByMetadataTypeUri.put(metadataTypeUri, metadataValues);
  }

  public void populateBooleanMetadata(String metadataTypeUri, JsonObject jsonObject) {
    JsonArray jsonValues = getAsArray(jsonObject, metadataTypeUri);
    if ((jsonValues != null) && (jsonValues.size() > 0)) {
      booleanMetadataValuesByMetadataTypeUri.put(metadataTypeUri,
          new BooleanMetadataValue(jsonValues.get(0).getAsBoolean().value()));
    }
  }

  public void populateAltLabels(String altLabelTypeUri, JsonValue jsonValue) {
    Collection<Label> altLabels = new HashSet<>();
    JsonArray jsonAltLabels = jsonValue.getAsArray();
    if (jsonAltLabels != null) {
      for (JsonValue jsonAltLabel : jsonAltLabels) {
        String labelUri = jsonAltLabel.getAsObject().getString("@id");
        JsonValue literalForm =
            jsonAltLabel.getAsObject().get("skosxl:literalForm").getAsArray().get(0).getAsObject();
        String languageCode = literalForm.getAsObject().getString("@language");
        String value = literalForm.getAsObject().getString("@value");
        Label label = new Label(labelUri, languageCode, value);
        altLabels.add(label);
      }
    }
    altLabelsByUri.put(altLabelTypeUri, altLabels);
  }

  public Collection<Label> getAltLabels(String uri) {
    return altLabelsByUri.get(uri);
  }

  public Label getPrefLabelByLanguageAndValue(String languageCode, String value) {
    return getByLanguageAndValue(prefLabelsByLanguageAndValue, languageCode, value);
  }

  private <T> T getByLanguageAndValue(Map<String, Map<String, T>> mapByLanguageAndName,
      String languageCode, String value) {
    Map<String, T> mapByValue = mapByLanguageAndName.get(languageCode);
    if (mapByValue == null) {
      return null;
    }
    return mapByValue.get(value);

  }

  private <T> void addByLanguageAndValue(Map<String, Map<String, T>> mapByLanguageAndValue,
      String languageCode, String name, T t) {
    Map<String, T> mapByValue = mapByLanguageAndValue.get(languageCode);
    if (mapByValue == null) {
      mapByValue = new HashMap<>();
      mapByLanguageAndValue.put(languageCode, mapByValue);
    }
    mapByValue.put(name, t);
  }

  public Concept(OEClientReadOnly oeClient, String uri, List<Label> labelList) {
    this.oeClient = oeClient;
    this.uri = uri;
    this.jsonObject = null;
    prefLabels.addAll(labelList);
  }

  private final Map<String, Identifier> identifiers = new HashMap<>();

  public void addIdentifier(Identifier identifier) {
    identifiers.put(identifier.getUri(), identifier);
  }

  public void setGuid(String guid) {
    identifiers.put(GUID_RELATIONSHIP_URI, new Identifier(GUID_RELATIONSHIP_URI, guid));
  }

  public String getGuid() {
    Identifier guidIdentifier = identifiers.get(GUID_RELATIONSHIP_URI);
    if (guidIdentifier == null) {
      return null;
    } else {
      return guidIdentifier.getValue();
    }
  }

  public Identifier getIdentifier(String relationshipUri) {
    return identifiers.get(relationshipUri);
  }

  public Collection<Identifier> getIdentifiers() {
    return identifiers.values();
  }

  @Override
  public String toString() {
    return this.asJson();
  }

  /**
   * Return the raw JSON for a server-loaded concept, or a string representation for manually created concepts.
   *
   * @return the concept JSON or fallback string representation
   */
  public String asJson() {
    if (jsonObject == null) {
      StringBuilder stringBuilder = new StringBuilder("Concept [uri=");
      stringBuilder.append(uri);
      stringBuilder.append(", prefLabels=");
      stringBuilder.append(prefLabels);
      stringBuilder.append("]");
      return stringBuilder.toString();
    }
    return JSON.toStringFlat(jsonObject);
  }

  public Collection<Label> getPrefLabels() {
    return prefLabels;
  }

  public void addClass(String classUri) {
    types.add(classUri);
  }

  public void addClasses(Collection<String> classUris) {
    types.addAll(classUris);
  }

  public void removeClass(String classUri) {
    types.remove(classUri);
  }

  public void removeClasses(Collection<String> classUris) {
    types.removeAll(classUris);
  }

  public Collection<String> getClassUris() {
    return Collections.unmodifiableCollection(types);
  }

  /**
   * Add an alt label under the specified label type URI (e.g. "skosxl:altLabel" or a custom type).
   * These alt labels will be included when the concept is created via createConcept or createConceptBelowConcept.
   *
   * @param labelTypeUri the label relationship type URI
   * @param label the label to add
   */
  public void addAltLabel(String labelTypeUri, Label label) {
    altLabelsByUri.computeIfAbsent(labelTypeUri, k -> new HashSet<>()).add(label);
  }

  /**
   * Add multiple alt labels under the specified label type URI.
   *
   * @param labelTypeUri the label relationship type URI
   * @param labels the labels to add
   */
  public void addAltLabels(String labelTypeUri, Collection<Label> labels) {
    altLabelsByUri.computeIfAbsent(labelTypeUri, k -> new HashSet<>()).addAll(labels);
  }

  /**
   * Get all alt labels grouped by their label type URI.
   * @return map of label type URI to collection of labels
   */
  public Map<String, Collection<Label>> getAltLabelsByUri() {
    return altLabelsByUri;
  }

  /**
   * Add an associative relationship to another concept, to be included at creation time.
   *
   * @param relationshipTypeUri the relationship type URI (e.g. "skos:related" or a custom URI)
   * @param targetConceptUri the URI of the target concept
   */
  public void addRelationship(String relationshipTypeUri, String targetConceptUri) {
    relatedConceptUrisByRelationship.computeIfAbsent(relationshipTypeUri, k -> new HashSet<>()).add(targetConceptUri);
  }

  /**
   * Get all relationships set for creation purposes.
   * @return map of relationship type URI to collection of target concept URIs
   */
  public Map<String, Collection<String>> getRelationships() {
    return relatedConceptUrisByRelationship;
  }

  public void populateClasses(JsonObject jsonObject) {
    types.clear();

    JsonArray jsonTypes = jsonObject.get("@type").getAsArray();
    if (jsonTypes != null) {
      for (JsonValue jsonType : jsonTypes) {
        types.add(jsonType.getAsString().value());
      }
    }
  }

}

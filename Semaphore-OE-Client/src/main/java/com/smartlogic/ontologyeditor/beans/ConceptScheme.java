// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartlogic.ontologyeditor.OEClientReadOnly;

public class ConceptScheme extends AbstractBeanFromJson {
  protected final static Logger logger = LoggerFactory.getLogger(ConceptScheme.class);

  private Collection<String> types = new HashSet<>();
  private Collection<Label> prefLabels = new HashSet<>();
  private Collection<String> topConceptUris = new HashSet<>();

  public ConceptScheme(OEClientReadOnly oeClient, JsonObject jsonObject) {
    logger.debug("ConceptScheme - entry: {}", jsonObject);
    this.uri = jsonObject.get("@id").getAsString().value();

    JsonArray jsonLabels = jsonObject.get("rdfs:label").getAsArray();
    for (JsonValue jsonLabel : jsonLabels) {
      JsonObject jsonLiteral = jsonLabel.getAsObject();

      String prefLabelValue = getAsString(jsonLiteral, "@value");
      String prefLabelLangCode = getAsString(jsonLiteral, "@language");

      prefLabels.add(new Label(null, prefLabelLangCode, prefLabelValue));
    }

    topConceptUris = new HashSet<>();
    JsonValue topConceptsValue = jsonObject.get("skos:hasTopConcept");
    if (topConceptsValue != null) {
      JsonArray topConcepts = topConceptsValue.getAsArray();
      for (JsonValue topConcept : topConcepts) {
        JsonObject topConceptObject = topConcept.getAsObject();
        String topConceptUri = topConceptObject.get("@id").getAsString().value();

        topConceptUris.add(topConceptUri);
      }
    }

    logger.info("ConceptScheme - exit: {}", this.uri);

  }

  public ConceptScheme(OEClientReadOnly oeClient, String uri, List<Label> labelList) {
    this.oeClient = oeClient;
    this.uri = uri;
    prefLabels.addAll(labelList);
  }

  /**
   * Get the URIs of the top concepts that belong to this concept scheme.
   *
   * @return the top concept URIs
   */
  public Collection<String> getTopConceptUris() {
    return topConceptUris;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("Concept:");
    stringBuilder.append(this.uri).append(" [");
    String sep = "";
    for (String type : types) {
      stringBuilder.append(sep).append(type);
      sep = ", ";
    }
    stringBuilder.append("] - [");
    for (Label prefLabel : prefLabels) {
      stringBuilder.append(" \"").append(prefLabel.toString()).append("\"");
    }
    stringBuilder.append("] - [ ");
    for (String topConceptUri : topConceptUris) {
      stringBuilder.append(" \"").append(topConceptUri).append("\"");
    }
    stringBuilder.append("] ");
    return stringBuilder.toString();
  }

  public Collection<Label> getPrefLabels() {
    return prefLabels;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ConceptScheme that = (ConceptScheme) o;
    return Objects.equals(types, that.types) && Objects.equals(prefLabels, that.prefLabels) && Objects.equals(topConceptUris, that.topConceptUris);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), types, prefLabels, topConceptUris);
  }
}

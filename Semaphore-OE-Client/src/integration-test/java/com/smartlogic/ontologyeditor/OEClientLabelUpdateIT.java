// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for label update operations.
 * Tests updating existing labels on concepts.
 */
public class OEClientLabelUpdateIT extends AbstractModelScopedIT {

  @Test
  public void updatePrefLabelOnConcept() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Original")));
    oeClient.createConcept(scheme.getUri(), concept);

    concept = oeClient.getConcept(concept.getUri()); // Refresh the concept to ensure we have the latest state
    Label existingLabel = concept.getPrefLabels().iterator().next();
    String newLabelValue = "Étiquette Française";
    String newLanguage = "fr";

    oeClient.updateLabel(existingLabel, newLanguage, newLabelValue);

    assertLabelExists(concept, newLanguage, newLabelValue);
  }

  @Test
  public void updateLabelWithTypeOnConcept() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Original")));
    oeClient.createConcept(scheme.getUri(), concept);
    concept = oeClient.getConcept(concept.getUri()); // Refresh the concept to ensure we have the latest state
    Label existingLabel = concept.getPrefLabels().iterator().next();
    String relationshipTypeUri = "http://www.w3.org/2008/05/skos-xl#prefLabel";
    String newLabelValue = "Updated with Type";
    String newLanguage = "en";

    oeClient.updateLabel(existingLabel, concept.getUri(), relationshipTypeUri, newLanguage, newLabelValue);

    assertLabelExists(concept, newLanguage, newLabelValue);
  }

  private void assertLabelExists(Concept concept, String languageCode, String value) throws OEClientException {
    Concept refreshedConcept = oeClient.getConcept(concept.getUri());
    boolean labelExists = refreshedConcept.getPrefLabels().stream()
            .anyMatch(label -> label.getLanguageCode().equals(languageCode) && label.getValue().equals(value));
    if (!labelExists) {
      throw new AssertionError("Label with language '" + languageCode + "' and value '" + value + "' not found on concept.");
    }
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "LabelUpdateScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID().toString();
  }
}


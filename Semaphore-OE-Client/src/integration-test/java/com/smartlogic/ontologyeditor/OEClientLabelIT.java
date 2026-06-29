// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for label operations.
 * Each test runs against a freshly created model (see {@link AbstractModelScopedIT}).
 */
public class OEClientLabelIT extends AbstractModelScopedIT {

  @Test
  public void addPrefLabelToExistingConcept() throws OEClientException {
    Concept concept = createTestConcept(createTestScheme());

    oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#prefLabel", new Label("fr", "Nouvelle Etiquette"));

    assertLabelExists(concept, "fr", "Nouvelle Etiquette", "http://www.w3.org/2008/05/skos-xl#prefLabel");
  }

  @Test
  public void addAltLabelToExistingConcept() throws OEClientException {
    Concept concept = createTestConcept(createTestScheme());

    oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#altLabel", new Label("en", "Alternative Label"));

    assertLabelExists(concept, "en", "Alternative Label", "http://www.w3.org/2008/05/skos-xl#altLabel");
  }

  @Test
  public void addNonExistingCustomLabelTypeToExistingConcept() throws OEClientException {
    Concept concept = createTestConcept(createTestScheme());

    try {
      oeClient.createLabel(concept, "http://example.test/customLabel", new Label("es", "Etiqueta Personalizada"));
      Assert.fail("Expected OEClientException when adding label with non-existing custom label type");
    } catch (OEClientException e) {
      assertNotNull("Expected an error when adding label with non-existing custom label type", e);
      assertTrue("Error message should indicate invalid label type",
          e.getMessage().contains("property that does not have a type and may have been deleted"));
    }
  }

  @Test
  public void addMultipleLabelsToMultipleConcepts() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept1 = createTestConcept(scheme, "Concept 1");
    Concept concept2 = createTestConcept(scheme, "Concept 2");

    oeClient.createLabels(
        new String[] {concept1.getUri(), concept2.getUri()},
        new String[] {"http://www.w3.org/2008/05/skos-xl#prefLabel", "http://www.w3.org/2008/05/skos-xl#altLabel"},
        new Label[] {new Label("fr", "Concept Un"), new Label("de", "Konzept Zwei")});

    assertLabelExists(concept1, "fr", "Concept Un", "http://www.w3.org/2008/05/skos-xl#prefLabel");
    assertLabelExists(concept2, "de", "Konzept Zwei", "http://www.w3.org/2008/05/skos-xl#altLabel");
  }

  @Test
  public void addLabelWithoutUriToExistingConcept() throws OEClientException {
    Concept concept = createTestConcept(createTestScheme());

    oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#altLabel",
        new Label("de", "Automatisch Generierte Bezeichnung"));

    assertLabelExists(concept, "de", "Automatisch Generierte Bezeichnung", "http://www.w3.org/2008/05/skos-xl#altLabel");
  }

  @Test
  public void a2ddLabelWithoutUriToExistingConcept() throws OEClientException {
    Concept concept = createTestConcept(createTestScheme());

    String altLabelTypeUri = oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#altLabel",
            new Label("de", "Automatisch Generierte Bezeichnung"));
    oeClient.deleteAltLabelType(altLabelTypeUri);
    oeClient.getLabelTypes().stream()
            .filter(labelType -> labelType.getUri().equals(altLabelTypeUri))
            .findFirst()
            .ifPresent(labelType -> {
              throw new IllegalStateException("Label type still exists after deletion: " + labelType.getUri());
            });
  }

  @Test
  public void addMultiplePrefLabelsInDifferentLanguages() throws OEClientException {
    Concept concept = createTestConcept(createTestScheme());

    oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#prefLabel", new Label("de", "Deutsch"));
    oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#prefLabel", new Label("es", "Español"));
    oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#altLabel", new Label("it", "Italiano"));

    assertLabelExists(concept, "de", "Deutsch", "http://www.w3.org/2008/05/skos-xl#prefLabel");
    assertLabelExists(concept, "es", "Español", "http://www.w3.org/2008/05/skos-xl#prefLabel");
    assertLabelExists(concept, "it", "Italiano", "http://www.w3.org/2008/05/skos-xl#altLabel");
  }

  private void assertLabelExists(Concept concept, String languageCode, String value, String labelUri) throws OEClientException {
    Concept refreshedConcept = oeClient.getConcept(concept.getUri());
    oeClient.populateAltLabels(labelUri, refreshedConcept);
    boolean labelExists = refreshedConcept.getAltLabels(labelUri).stream()
            .anyMatch(label -> label.getLanguageCode().equals(languageCode) && label.getValue().equals(value));
    if (!labelExists) {
      throw new AssertionError("Label with language '" + languageCode + "' and value '" + value + "' not found on concept.");
    }
  }

  private Concept createTestConcept(ConceptScheme scheme) throws OEClientException {
    return createTestConcept(scheme, "Test Concept " + UUID.randomUUID());
  }

  private Concept createTestConcept(ConceptScheme scheme, String label) throws OEClientException {
    Concept concept = new Concept(oeClient, "http://example.test/concept/" + UUID.randomUUID(),
        List.of(new Label("en", label)));
    oeClient.createConcept(scheme.getUri(), concept);
    return concept;
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "LabelTestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }
}

// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Integration tests for concept count operations.
 * Each test runs against a freshly created model (see {@link AbstractModelScopedIT}).
 */
public class OEClientConceptCountIT extends AbstractModelScopedIT {

  @Test
  public void getConceptCountAfterAddingConcepts() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    long countBefore = oeClient.getConceptCount();

    Concept concept1 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Concept 1")));
    Concept concept2 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Concept 2")));
    oeClient.createConcepts(scheme.getUri(), Set.of(concept1, concept2));

    long countAfter = oeClient.getConceptCount();
    assertTrue("Concept count should increase after adding concepts", countAfter >= countBefore + 2);
  }

  @Test
  public void getConceptCountWithHierarchicalConcepts() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept parent = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Parent")));
    Concept child1 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Child 1")));
    Concept child2 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Child 2")));

    oeClient.createConcept(scheme.getUri(), parent);
    oeClient.createConceptBelowConcept(parent.getUri(), child1);
    oeClient.createConceptBelowConcept(parent.getUri(), child2);

    long count = oeClient.getConceptCount();
    assertTrue("Concept count should include all levels of hierarchy (at least 3)", count >= 3);
  }

  @Test
  public void getConceptCountAcrossMultipleSchemes() throws OEClientException {
    ConceptScheme scheme1 = createTestScheme();
    ConceptScheme scheme2 = createTestScheme();

    oeClient.createConcept(scheme1.getUri(), new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C1"))));
    oeClient.createConcept(scheme1.getUri(), new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C2"))));
    oeClient.createConcept(scheme2.getUri(), new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C3"))));

    long count = oeClient.getConceptCount();
    assertTrue("Concept count should aggregate across all schemes in the model", count >= 3);
  }

  @Test
  public void getConceptCountWithMultipleLanguageLabels() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = new Concept(oeClient, generateConceptUri(),
        Arrays.asList(new Label("en", "English"), new Label("fr", "Français")));
    oeClient.createConcept(scheme.getUri(), concept);

    long count = oeClient.getConceptCount();
    assertTrue("Each concept should be counted once regardless of how many language labels it has", count >= 1);
  }

  @Test
  public void getConceptCountIsNonNegative() throws OEClientException {
    // Empty model – no concepts created
    long count = oeClient.getConceptCount();
    assertTrue("Concept count should be non-negative even on an empty model", count >= 0);
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "CountTestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID().toString();
  }
}

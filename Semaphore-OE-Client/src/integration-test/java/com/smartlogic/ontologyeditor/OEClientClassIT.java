// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for concept class operations.
 * Tests adding and removing classes from concepts.
 */
public class OEClientClassIT extends AbstractModelScopedIT {

  @Test
  public void addCustomClassToConcept() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme.getUri());

    String customClassUri = "http://example.test/customClass";
    oeClient.createClass(new Label("en", "Custom Class"), customClassUri, null);
    oeClient.addClass(concept, customClassUri);

    Concept updatedConcept = oeClient.getConcept(concept.getUri());
    updatedConcept.getClassUris().stream().filter(uri -> uri.equals(customClassUri)).findFirst().orElseThrow(() -> new AssertionError("Custom class URI not found in concept classes"));
  }

  @Test
  public void addClassAndThenRemove() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme.getUri());

    String customClassUri = "http://example.test/customClass";
    oeClient.createClass(new Label("en", "Custom Class"), customClassUri, null);
    oeClient.addClass(concept, customClassUri);
    oeClient.removeClass(concept, customClassUri);

    Concept updatedConcept = oeClient.getConcept(concept.getUri());
    assertNotNull(updatedConcept);
    assertNotNull(updatedConcept.getClassUris());
    assertFalse(updatedConcept.getClassUris().contains(customClassUri));
  }

  @Test
  public void addMultipleClassesToConcept() throws OEClientException {
    ConceptScheme scheme = createTestScheme();

    // Add multiple custom classes
    oeClient.createClass(new Label("en", "Custom Class"), "http://example.test/class1", null);
    oeClient.createClass(new Label("en", "Custom Class 2"), "http://example.test/class2", null);
    oeClient.createClass(new Label("en", "Custom Class 3"), "http://example.test/class3", null);
    Concept concept = createTestConcept(scheme.getUri(), Set.of("http://example.test/class1", "http://example.test/class2", "http://example.test/class3"));

    Concept updatedConcept = oeClient.getConcept(concept.getUri());
    assertNotNull(updatedConcept);
    assertNotNull(updatedConcept.getClassUris());
    assertEquals(3, updatedConcept.getClassUris().size());
  }

  @Test
  public void removeClassFromMultipleClasses() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    oeClient.createClass(new Label("en", "Custom Class"), "http://example.test/class1", null);
    Concept concept = createTestConcept(scheme.getUri(), Set.of("http://example.test/class1"));
    oeClient.removeClass(concept, "http://example.test/class1");

    Concept updatedConcept = oeClient.getConcept(concept.getUri());
    assertNotNull(updatedConcept);
    assertNotNull(updatedConcept.getClassUris());
    assertFalse(updatedConcept.getClassUris().contains("http://example.test/class1"));  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "ClassTestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private Concept createTestConcept(String conceptSchemeUri, Set<String> classUris) throws OEClientException {
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Test Concept")));
    concept.addClasses(classUris);
    oeClient.createConcept(conceptSchemeUri, concept);
    return concept;
  }

  private Concept createTestConcept(String conceptSchemeUri) throws OEClientException {
    return createTestConcept(conceptSchemeUri, Set.of());
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID().toString();
  }
}


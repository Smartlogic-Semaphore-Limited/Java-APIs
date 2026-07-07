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
 * Integration tests for relationship operations.
 * Each test runs against a freshly created model (see {@link AbstractModelScopedIT}).
 */
public class OEClientRelationshipIT extends AbstractModelScopedIT {

  @Test
  public void createAssociativeRelationshipBetweenConcepts() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept1 = createTestConcept(scheme, "Source");
    Concept concept2 = createTestConcept(scheme, "Target");

    oeClient.createRelationship("http://www.w3.org/2004/02/skos/core#related", concept1, concept2);

    assertNotNull("Source concept should be retrievable after creating relationship",
        oeClient.getConcept(concept1.getUri()));
  }

  @Test
  public void createCustomRelationshipBetweenConcepts() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept1 = createTestConcept(scheme, "Parent Entity");
    Concept concept2 = createTestConcept(scheme, "Child Entity");

    oeClient.createRelationshipType(new Label("en", "has component"), "http://example.test/hasComponent", new Label("en", "is component of"), "http://example.test/isComponentOf");
    oeClient.createRelationship("http://example.test/hasComponent", concept1, concept2);

    assertNotNull("Concept should be retrievable after creating custom relationship",
        oeClient.getConcept(concept1.getUri()));
  }

  @Test
  public void createConceptWithRelationshipAtCreationTime() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept targetConcept = createTestConcept(scheme, "Target");

    Concept sourceConcept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Source")));
    sourceConcept.addRelationship("http://www.w3.org/2004/02/skos/core#related", targetConcept.getUri());
    oeClient.createConcept(scheme.getUri(), sourceConcept);

    assertNotNull("Concept created with relationships should be retrievable",
        oeClient.getConcept(sourceConcept.getUri()));
  }

  @Test
  public void createMultipleRelationshipTypesFromSingleConcept() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept sourceConcept = createTestConcept(scheme, "Hub");
    Concept relatedConcept = createTestConcept(scheme, "Related");
    Concept similarConcept = createTestConcept(scheme, "Similar");
    oeClient.createRelationshipType(new Label("en", "similar to"), "http://example.test/similar", new Label("en", "similar to inv"), "http://example.test/similarTo");
    oeClient.createRelationship("http://www.w3.org/2004/02/skos/core#related", sourceConcept, relatedConcept);
    oeClient.createRelationship("http://example.test/similar", sourceConcept, similarConcept);

    assertNotNull("Concept with multiple relationship types should be retrievable",
        oeClient.getConcept(sourceConcept.getUri()));
  }

  @Test
  public void createHierarchicalConceptStructure() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept level1 = createTestConcept(scheme, "Level 1");
    Concept level2a = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Level 2a")));
    Concept level2b = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Level 2b")));
    Concept level3 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Level 3")));

    oeClient.createConceptBelowConcept(level1.getUri(), level2a);
    oeClient.createConceptBelowConcept(level1.getUri(), level2b);
    oeClient.createConceptBelowConcept(level2a.getUri(), level3);

    assertNotNull("Level 1 concept should be retrievable", oeClient.getConcept(level1.getUri()));
    assertNotNull("Level 2 concept should be retrievable", oeClient.getConcept(level2a.getUri()));
    assertNotNull("Level 3 concept should be retrievable", oeClient.getConcept(level3.getUri()));
  }

  @Test
  public void deleteRelationshipBetweenConcepts() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept1 = createTestConcept(scheme, "Concept A");
    Concept concept2 = createTestConcept(scheme, "Concept B");

    oeClient.createRelationship("http://www.w3.org/2004/02/skos/core#related", concept1, concept2);
    oeClient.deleteRelationship("http://www.w3.org/2004/02/skos/core#related", concept1, concept2);

    assertNotNull("Concept should be retrievable after deleting relationship",
        oeClient.getConcept(concept1.getUri()));
  }

  private Concept createTestConcept(ConceptScheme scheme, String label) throws OEClientException {
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", label)));
    oeClient.createConcept(scheme.getUri(), concept);
    return concept;
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "RelationshipTestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID().toString();
  }
}

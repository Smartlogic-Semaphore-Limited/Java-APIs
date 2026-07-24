// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for relationship type management.
 * Tests creation of relationship types and label relationship types.
 */
public class OEClientRelationshipTypeIT extends AbstractModelScopedIT {

  @Test
  public void createRelationshipType() throws OEClientException {
    String forwardUri = "http://example.test/relType_" + UUID.randomUUID();
    String inverseUri = "http://example.test/invRelType_" + UUID.randomUUID();
    Label forwardLabel = new Label("en", "Forward Relationship");
    Label inverseLabel = new Label("en", "Inverse Relationship");

    oeClient.createRelationshipType(forwardLabel, forwardUri, inverseLabel, inverseUri);

    oeClient.getAssociativeRelationshipTypes().stream()
        .filter(rt -> rt.getUri().equals(forwardUri) || rt.getUri().equals(inverseUri))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Relationship type not found after creation"));
  }

  @Test
  public void createLabelRelationshipType() throws OEClientException {
    String labelTypeUri = "http://example.test/customLabelType_" + UUID.randomUUID();
    Label label = new Label("en", "Custom Label Type");

    oeClient.createLabelRelationshipType(label, labelTypeUri);

    oeClient.getLabelTypes().stream()
        .filter(rt -> rt.getUri().equals(labelTypeUri))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Label relationship type not found after creation"));
  }

  @Test
  public void createSymmetricRelationshipType() throws OEClientException {
    String symmetricUri = "http://example.test/symmetricRelType_" + UUID.randomUUID();
    Label label = new Label("en", "Symmetric Relationship");

    oeClient.createSymmetricRelationshipType(label, symmetricUri);

    oeClient.getAssociativeRelationshipTypes().stream()
        .filter(rt -> rt.getUri().equals(symmetricUri))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Symmetric relationship type not found after creation"));
  }

  @Test
  public void symmetricRelationshipIsInstantiatedInBothDirections() throws OEClientException {
    String symmetricUri = "http://example.test/symmetricRelType_" + UUID.randomUUID();
    Label label = new Label("en", "Married To");
    oeClient.createSymmetricRelationshipType(label, symmetricUri);

    ConceptScheme scheme = createTestScheme();
    Concept concept1 = createTestConcept(scheme, "Concept A");
    Concept concept2 = createTestConcept(scheme, "Concept B");

    // Only the forward direction (concept1 -> concept2) is created explicitly.
    oeClient.createRelationship(symmetricUri, concept1, concept2);

    Concept reloadedConcept1 = oeClient.getConcept(concept1.getUri());
    Concept reloadedConcept2 = oeClient.getConcept(concept2.getUri());

    assertTrue("Forward relationship should be present on concept1",
        reloadedConcept1.getRelatedConceptUris(symmetricUri).contains(concept2.getUri()));
    assertTrue(
        "Because the relationship type is symmetric, concept2 should also report the reverse "
            + "relationship back to concept1 even though it was never created explicitly",
        reloadedConcept2.getRelatedConceptUris(symmetricUri).contains(concept1.getUri()));
  }

  private Concept createTestConcept(ConceptScheme scheme, String label) throws OEClientException {
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", label)));
    oeClient.createConcept(scheme.getUri(), concept);
    return concept;
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "SymmetricRelTypeTestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID();
  }

}


// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration tests for concept creation error handling.
 * Tests various error scenarios and boundary conditions in concept creation.
 */
public class OEClientConceptCreationErrorIT extends AbstractModelScopedIT {

  @Test
  public void createConceptsWithNullConceptListFails() {
    try {
      oeClient.createConcepts("http://example.test/scheme", (List<Concept>) null, Collections.emptyList());
      fail("Should throw OEClientException for null concepts list");
    } catch (OEClientException e) {
      assertEquals("Exception should be thrown for null concepts", "concepts cannot be null", e.getMessage());
    }
  }

  @Test
  public void createConceptsWithMismatchedMetadataListFails() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept c1 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C1")));
    Concept c2 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C2")));

    Map<String, Collection<MetadataValue>> meta1 = new HashMap<>();
    meta1.put("http://example.test/meta", List.of(new MetadataValue("", "val1")));
    // Provide only 1 metadata map for 2 concepts
    List<Map<String, Collection<MetadataValue>>> metas = List.of(meta1);

    try {
      oeClient.createConcepts(scheme.getUri(), List.of(c1, c2), metas);
      fail("Should throw OEClientException for mismatched metadata list");
    } catch (OEClientException e) {
      assertNotNull("Exception should be thrown for mismatched metadata", e);
    }
  }

  @Test
  public void createConceptsWithEmptyConceptsList() throws OEClientException {
    ConceptScheme scheme = createTestScheme();

    // Should complete without error (no-op)
    oeClient.createConcepts(scheme.getUri(), Collections.emptyList(), Collections.emptyList());

    assertNotNull("Empty concept list should be handled gracefully", scheme.getUri());
  }

  @Test
  public void createConceptsWithNullConceptInList() throws OEClientException {
    ConceptScheme scheme;
    try {
      scheme = createTestScheme();
    } catch (OEClientException e) {
      throw new RuntimeException(e);
    }
    ConceptScheme finalScheme = scheme;

    try {
      oeClient.createConcepts(List.of((Concept) null), List.of(finalScheme.getUri()), List.of(true));
      fail("Should throw IllegalArgumentException for null concept in list");
    } catch (NullPointerException e) {
      assertNotNull("Exception should be thrown for null concept in list", e);
    }
  }

  @Test
  public void createConceptsWithMismatchedParentUrisList() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept c1 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C1")));
    Concept c2 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C2")));

    try {
      // 2 concepts but only 1 parent URI
      oeClient.createConcepts(
          List.of(c1, c2),
          List.of(scheme.getUri()),
          List.of(true, false)
      );
      fail("Should throw IllegalArgumentException for mismatched parentUris list");
    } catch (IllegalArgumentException e) {
      assertEquals("concepts size (2) must match parentUris size (1)", e.getMessage());
    }
  }

  @Test
  public void createConceptsWithMismatchedAsTopConceptList() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept c1 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C1")));
    Concept c2 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C2")));

    try {
      // 2 concepts but only 1 asTopConcept flag
      oeClient.createConcepts(
          List.of(c1, c2),
          List.of(scheme.getUri(), scheme.getUri()),
          List.of(true)
      );
      fail("Should throw IllegalArgumentException for mismatched asTopConcept list");
    } catch (IllegalArgumentException e) {
      assertEquals("concepts size (2) must match asTopConcept size (1)", e.getMessage());
    }
  }

  @Test
  public void createConceptsWithValidMixedRelationships() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept topConcept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Top")));
    Concept childConcept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Child")));

    oeClient.createConcept(scheme.getUri(), topConcept);

    oeClient.createConcepts(
        List.of(childConcept),
        List.of(topConcept.getUri()),
        List.of(false)
    );

    Concept concept = oeClient.getConcept(topConcept.getUri());
    assertTrue(concept.getNarrowerConceptUris().contains(childConcept.getUri()));
  }

  @Test
  public void createConceptsWithMetadataSucceeds() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept c1 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C1")));
    Concept c2 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "C2")));
    oeClient.createMetadataTypeString(new Label("en", "meta"), "http://example.test/meta");
    Map<String, Collection<MetadataValue>> meta1 = new HashMap<>();
    meta1.put("http://example.test/meta", List.of(new MetadataValue("", "val1")));

    Map<String, Collection<MetadataValue>> meta2 = new HashMap<>();
    meta2.put("http://example.test/meta", List.of(new MetadataValue("", "val2")));

    oeClient.createConcepts(
        List.of(c1, c2),
        List.of(scheme.getUri(), scheme.getUri()),
        List.of(true, true),
        List.of(meta1, meta2)
    );
    oeClient.getAllConcepts().forEach(concept -> {
        try {
          oeClient.populateMetadata("http://example.test/meta", concept);
          assertEquals(1, concept.getMetadata("http://example.test/meta").size());
        } catch (OEClientException e) {
            throw new RuntimeException(e);
        }

    });

  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "ErrorTestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID();
  }
}



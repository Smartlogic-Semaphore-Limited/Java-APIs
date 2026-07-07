// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for concept creation with metadata.
 * Each test runs against a freshly created model (see {@link AbstractModelScopedIT}).
 */
public class OEClientConceptMetadataIT extends AbstractModelScopedIT {

  @Test
  public void createConceptWithStringMetadata() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept testConcept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Test Concept")));

    Map<String, Collection<MetadataValue>> metadata = new HashMap<>();
    metadata.put("http://purl.org/dc/elements/1.1/description", List.of(new MetadataValue("en", "A test concept")));

    oeClient.createConcept(scheme.getUri(), testConcept, metadata);

    Concept retrieved = oeClient.getConcept(testConcept.getUri());
    assertNotNull("Concept should be retrievable", retrieved);
    assertEquals("URI should match", testConcept.getUri(), retrieved.getUri());
  }

  @Test
  public void createConceptBelowParentWithMetadata() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept parentConcept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Parent")));
    Concept childConcept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Child")));

    Map<String, Collection<MetadataValue>> parentMetadata = new HashMap<>();
    parentMetadata.put("http://purl.org/dc/elements/1.1/type", List.of(new MetadataValue("", "Category")));
    oeClient.createConcept(scheme.getUri(), parentConcept, parentMetadata);

    Map<String, Collection<MetadataValue>> childMetadata = new HashMap<>();
    childMetadata.put("http://purl.org/dc/elements/1.1/type", List.of(new MetadataValue("", "Item")));
    oeClient.createConceptBelowConcept(parentConcept.getUri(), childConcept, childMetadata);

    Concept retrievedChild = oeClient.getConcept(childConcept.getUri());
    assertNotNull("Child concept should be retrievable", retrievedChild);
  }

  @Test
  public void createMultipleConceptsWithMixedRelationships() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept1 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Concept 1")));
    Concept concept2 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Concept 2")));
    Concept concept3 = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Concept 3")));

    oeClient.createConcepts(
        List.of(concept1, concept2, concept3),
        List.of(scheme.getUri(), concept1.getUri(), concept1.getUri()),
        List.of(true, false, false)
    );

    assertNotNull("Concept 1 should exist", oeClient.getConcept(concept1.getUri()));
    assertNotNull("Concept 2 should exist", oeClient.getConcept(concept2.getUri()));
    assertNotNull("Concept 3 should exist", oeClient.getConcept(concept3.getUri()));
  }

  @Test
  public void createConceptWithAltLabels() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Primary")));
    concept.addAltLabel("http://www.w3.org/2008/05/skos-xl#altLabel", new Label("en", "Alternative Name"));
    concept.addAltLabel("http://www.w3.org/2008/05/skos-xl#altLabel", new Label("fr", "Nom Alternatif"));

    oeClient.createConcept(scheme.getUri(), concept);

    assertNotNull("Concept with alt labels should be retrievable", oeClient.getConcept(concept.getUri()));
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "TestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID().toString();
  }
}

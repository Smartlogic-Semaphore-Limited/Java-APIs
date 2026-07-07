// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for deletion operations.
 * Tests deletion of concepts, schemes, and labels.
 */
public class OEClientConceptDeletionIT extends AbstractModelScopedIT {

  @Test
  public void deleteConceptFromScheme() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "To Delete")));
    oeClient.createConcept(scheme.getUri(), concept);
    assertEquals(1, oeClient.getConceptScheme(scheme.getUri()).getTopConceptUris().size());
    oeClient.deleteConcept(concept);
    assertEquals(0, oeClient.getConceptScheme(scheme.getUri()).getTopConceptUris().size());
  }

  @Test
  public void deleteConceptScheme() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    String schemeUri = scheme.getUri();

    oeClient.deleteConceptScheme(scheme);

    assertEquals(0, oeClient.getAllConceptSchemes().size());
  }

  @Test
  public void deleteConceptLabelFromConcept() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Test")));
    oeClient.createConcept(scheme.getUri(), concept);

    Label label = new Label("en", "Label to delete");
    String labelUri = oeClient.createLabel(concept, "http://www.w3.org/2008/05/skos-xl#altLabel", label);
    concept = oeClient.getConcept(concept.getUri());
    oeClient.populateAltLabels("http://www.w3.org/2008/05/skos-xl#altLabel", concept);
    assertEquals(1, concept.getAltLabelsByUri().get("http://www.w3.org/2008/05/skos-xl#altLabel").size());
    oeClient.deleteLabel("http://www.w3.org/2008/05/skos-xl#altLabel", concept, new Label(labelUri, label.getLanguageCode(), label.getValue()));
    concept = oeClient.getConcept(concept.getUri());
    oeClient.populateAltLabels("http://www.w3.org/2008/05/skos-xl#altLabel", concept);
    assertEquals(0, oeClient.getConcept(concept.getUri()).getAltLabelsByUri().size());
  }
  // TODO: Adjust and enable the test
//  @Test
  public void deleteConceptWithHierarchicalChildren() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept parent = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Parent")));
    Concept child = new Concept(oeClient, generateConceptUri(), List.of(new Label("en", "Child")));

    oeClient.createConcept(scheme.getUri(), parent);
    oeClient.createConceptBelowConcept(parent.getUri(), child);

    // Delete parent (should cascade/handle children)
    oeClient.deleteConceptWithSubtree(parent);

    assertConceptDeleted(parent.getUri());
    assertConceptDeleted(child.getUri());
  }

  private void assertConceptDeleted(String conceptUri) {
    try {
      Concept concept = oeClient.getConcept(conceptUri);
      if (concept != null) {
        fail("Concept should be deleted but was returned: " + conceptUri);
      }
    } catch (OEClientException e) {
      // Current client behavior for missing resources is an OEClientException with a 404 message.
      assertTrue("Expected 404 when concept is deleted, but got: " + e.getMessage(),
          e.getMessage() != null && e.getMessage().contains("404"));
    }
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "DeletionTestScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }

  private String generateConceptUri() {
    return "http://example.test/concept/" + UUID.randomUUID().toString();
  }
}


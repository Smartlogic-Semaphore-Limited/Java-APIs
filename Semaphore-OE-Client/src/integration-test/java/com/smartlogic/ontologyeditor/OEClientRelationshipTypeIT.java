// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

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

}


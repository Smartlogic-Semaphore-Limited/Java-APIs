// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for concept scheme management.
 * Tests updating concept scheme labels.
 */
public class OEClientConceptSchemeManagementIT extends AbstractModelScopedIT {

  @Test
  public void updateConceptSchemeLabel() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Label oldLabel = scheme.getPrefLabels().iterator().next();

    Label newLabel = new Label("fr", "chéma Mis à Jour");
    scheme = oeClient.getConceptScheme(scheme.getUri());
    oeClient.updateConceptScheme(scheme, oldLabel, newLabel);

    oeClient.getConceptScheme(scheme.getUri()).getPrefLabels().stream()
        .filter(label -> label.getLanguageCode().equals("fr") && label.getValue().equals("chéma Mis à Jour"))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Updated label not found on concept scheme"));
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    return createTestScheme(false);
  }

  private ConceptScheme createTestScheme(boolean prefixed) throws OEClientException {
    String uniqueName = "SchemeMgmtScheme_" + UUID.randomUUID().toString().substring(0, 8);
    String schemeUri = prefixed ? "example:" + uniqueName : "http://example.test/scheme/" + uniqueName;
    ConceptScheme scheme = new ConceptScheme(oeClient, schemeUri,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }
}


// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ConceptSchemeTest {

  @Test
  public void getTopConceptUrisReturnsEmptyForManuallyCreated() {
    ConceptScheme conceptScheme = new ConceptScheme(null, "urn:scheme:test", List.of(new Label("en", "Test")));

    assertTrue(conceptScheme.getTopConceptUris().isEmpty());
  }
}

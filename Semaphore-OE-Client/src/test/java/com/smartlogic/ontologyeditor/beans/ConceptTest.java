// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConceptTest {

  @Test
  public void asJsonReturnsStringRepresentationWhenJsonObjectIsNull() {
    Concept concept = new Concept(List.of(new Label("en", "Hello")), List.of(), Collections.emptyMap());

    assertTrue(concept.asJson().contains("Hello"));
  }

  @Test
  public void addAltLabelsAddsAllLabelsUnderType() {
    Concept concept = new Concept(List.of(new Label("en", "Hello")), List.of(), Collections.emptyMap());

    concept.addAltLabels("skosxl:altLabel", List.of(new Label("en", "Alt1"), new Label("fr", "Alt2")));

    Map<String, Collection<Label>> altLabelsByUri = concept.getAltLabelsByUri();
    assertEquals(2, altLabelsByUri.get("skosxl:altLabel").size());
  }
}

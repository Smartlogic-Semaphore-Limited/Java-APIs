// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ModelTest {

  @Test
  public void constructorWithLanguagesSetsLanguagesList() {
    Model model = new Model(
        "urn:model:1",
        new Label("en", "My Model"),
        "comment",
        List.of(new ModelLanguage("urn:lang:en", new Label("en", "English"), "en")));

    assertEquals(1, model.getLanguages().size());
    assertEquals("en", model.getLanguages().get(0).getNotation());
  }

  @Test
  public void constructorWithoutLanguagesHasEmptyList() {
    Model model = new Model("urn:model:1", new Label("en", "My Model"), null);

    assertNotNull(model.getLanguages());
    assertTrue(model.getLanguages().isEmpty());
  }
}

// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ModelLanguageTest {

  @Test
  public void constructorWithExplicitValuesSetsFields() {
    ModelLanguage modelLanguage = new ModelLanguage("http://lang/en", new Label("en", "English"), "en");

    assertEquals("http://lang/en", modelLanguage.getUri());
    assertEquals("English", modelLanguage.getTitle().getValue());
    assertEquals("en", modelLanguage.getNotation());
  }
}

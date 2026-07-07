// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ModelLanguageTest {

  @Test
  public void constructorWithExplicitValuesSetsFields() {
    ModelLanguage modelLanguage = new ModelLanguage("http://lang/en", new Label("en", "English"), "en");

    assertEquals("http://lang/en", modelLanguage.getUri());
    assertEquals("English", modelLanguage.getTitle().getValue());
    assertEquals("en", modelLanguage.getNotation());
  }

  @Test
  public void fromJsonObjectParsesAllFields() {
    JsonObject json = JSON.parse("{\"@id\":\"http://lang/en\",\"dc:title\":[{\"@language\":\"en\",\"@value\":\"English\"}],\"skos:notation\":[{\"@value\":\"en\"}]}");

    ModelLanguage lang = new ModelLanguage(json);

    assertEquals("http://lang/en", lang.getUri());
    assertEquals("English", lang.getTitle().getValue());
    assertEquals("en", lang.getTitle().getLanguageCode());
    assertEquals("en", lang.getNotation());
  }

  @Test
  public void fromJsonObjectWithMissingTitleResultsInNullTitle() {
    JsonObject json = JSON.parse("{\"@id\":\"http://lang/en\",\"skos:notation\":[{\"@value\":\"en\"}]}");

    ModelLanguage lang = new ModelLanguage(json);

    assertNull(lang.getTitle());
    assertEquals("en", lang.getNotation());
  }

  @Test
  public void fromJsonObjectWithMissingNotationResultsInNullNotation() {
    JsonObject json = JSON.parse("{\"@id\":\"http://lang/en\",\"dc:title\":[{\"@language\":\"en\",\"@value\":\"English\"}]}");

    ModelLanguage lang = new ModelLanguage(json);

    assertEquals("English", lang.getTitle().getValue());
    assertNull(lang.getNotation());
  }
}

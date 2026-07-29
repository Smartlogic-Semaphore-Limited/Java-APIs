// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor.beans;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

  @Test
  public void constructorWithDefaultNamespaceSetsIt() {
    Model model = new Model("urn:model:1", new Label("en", "My Model"), "comment",
        "http://example.com/my-model#");

    assertEquals("http://example.com/my-model#", model.getDefaultNamespace());
    assertNotNull(model.getLanguages());
    assertTrue(model.getLanguages().isEmpty());
  }

  @Test
  public void constructorWithoutDefaultNamespaceLeavesItNull() {
    Model model = new Model("urn:model:1", new Label("en", "My Model"), null);

    assertNull(model.getDefaultNamespace());
  }

  @Test
  public void jsonConstructorParsesCommentAndDefaultNamespaceFromArrayValuedProperties() {
    JsonObject jsonObject = JSON.parse("{"
        + "\"meta:displayName\": {\"@value\": \"My Model\"},"
        + "\"meta:graphUri\": {\"@id\": \"model:fp1\"},"
        + "\"rdfs:comment\": [{\"@value\": \"A description\"}],"
        + "\"swa:defaultNamespace\": [{\"@value\": \"http://example.com/fp1#\"}]"
        + "}");

    Model model = new Model(jsonObject);

    assertEquals("A description", model.getComment());
    assertEquals("http://example.com/fp1#", model.getDefaultNamespace());
  }

  @Test
  public void jsonConstructorParsesCommentAndDefaultNamespaceFromSingleValuedProperties() {
    JsonObject jsonObject = JSON.parse("{"
        + "\"meta:displayName\": {\"@value\": \"My Model\"},"
        + "\"meta:graphUri\": {\"@id\": \"model:fp1\"},"
        + "\"rdfs:comment\": {\"@value\": \"A description\"},"
        + "\"swa:defaultNamespace\": {\"@value\": \"http://example.com/fp1#\"}"
        + "}");

    Model model = new Model(jsonObject);

    assertEquals("A description", model.getComment());
    assertEquals("http://example.com/fp1#", model.getDefaultNamespace());
  }

  @Test
  public void jsonConstructorLeavesCommentAndDefaultNamespaceNullWhenPropertiesAbsent() {
    JsonObject jsonObject = JSON.parse("{"
        + "\"meta:displayName\": {\"@value\": \"My Model\"},"
        + "\"meta:graphUri\": {\"@id\": \"model:fp1\"}"
        + "}");

    Model model = new Model(jsonObject);

    assertNull(model.getComment());
    assertNull(model.getDefaultNamespace());
  }
}


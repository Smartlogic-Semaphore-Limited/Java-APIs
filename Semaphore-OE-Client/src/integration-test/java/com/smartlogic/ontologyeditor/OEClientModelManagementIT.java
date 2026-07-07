// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;
import com.smartlogic.ontologyeditor.beans.ModelLanguage;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration tests for model management operations.
 * Tests model linking, class creation, and model updates.
 */
public class OEClientModelManagementIT extends AbstractModelScopedIT {

  @Test
  public void linkModelToAnotherModel() throws OEClientException {
    oeClient.createConceptScheme(new ConceptScheme(oeClient, "http://example.test/schemeForLinking", List.of(new Label("en", "Scheme for Linking"))));
    String modelLabel = "OE_CLIENT_EXAMPLE_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    String modelUri = "model:" + modelLabel;
    Model linkedModel = new Model(modelUri, new Label("", modelLabel), null);
    oeClient.createModel(linkedModel);

    try {
      oeClient.setModelUri(linkedModel.getUri());
      oeClient.linkModel(testModel.getUri());
      assertEquals(1, oeClient.getAllConceptSchemes().size());
    } finally {
      oeClient.deleteModel(linkedModel);
    }
  }

  @Test
  public void createCustomClass() throws OEClientException {
    Label classLabel = new Label("en", "CustomClass");
    String classUri = "http://example.test/customClass";

    oeClient.createClass(classLabel, classUri, null);
    oeClient.getConceptClasses().stream().filter(c -> c.getUri().equals(classUri)).findFirst().orElseThrow(() -> new AssertionError("Custom class not found"));
  }

  @Test
  public void updateModel() throws OEClientException {
      oeClient.updateModel(testModel, "My new display name");
    Model model = oeClient.getModel(testModel.getUri());
    assertEquals("My new display name", model.getLabel().getValue());
  }

  @Test
  public void addLanguage() throws OEClientException {
    oeClient.addLanguage("pl", "pl-pl");
    List<ModelLanguage> languages = oeClient.getModel(testModel.getUri()).getLanguages();
    assertEquals(2, languages.size());
    ModelLanguage language = languages.stream().filter(lang -> lang.getNotation().contains("pl")).findFirst().orElseThrow(() -> new AssertionError("Language not found"));
    assertEquals("pl-PL", language.getNotation());
    assertEquals("sem:Lang-pl-pl", language.getUri());
    assertEquals(new Label("en", "pl"), language.getTitle());
  }

  @Test
  public void addLanguage_invalidFormat() throws OEClientException {
    try {
      oeClient.addLanguage("pl", "pl-pl-pl");
      fail("Exception expected");
    } catch (OEClientException e){
      assertTrue(e.getMessage().contains("pl-PL-PL is not a valid language code according to BCP 47 specification."));
    }

  }

}


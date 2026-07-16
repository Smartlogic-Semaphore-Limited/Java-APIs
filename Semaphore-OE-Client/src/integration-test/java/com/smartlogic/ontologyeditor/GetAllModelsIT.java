// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;
import org.junit.After;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Integration test – retrieves the list of models from the live OE server and
 * verifies that a non-null collection is returned.
 *
 * <p>Skipped automatically when {@code OE_BASE_URL} / {@code OE_MODEL_URI} env
 * vars are not set. See {@link AbstractModelScopedIT} for configuration.
 */
public class GetAllModelsIT extends AbstractModelScopedIT {
    private static final Model EXPECTED_MY_EXAMPLE_MODEL = new Model("model:myExample", new Label("", "myExample"), null);
    @Test
    public void getAllModels_expectNoModels() throws OEClientException {
        List<Model> models = oeClient.getAllModels().stream().toList();
        assertNotNull("getAllModels() must return a non-null collection", models);
        assertEquals("Expected no models in the collection", 0, models.size());
    }

    @Test
    public void getModel_shortURI() throws OEClientException {
        oeClient.createModel(EXPECTED_MY_EXAMPLE_MODEL);
        try {
            Model model = oeClient.getModel("model:myExample");
            assertNotNull("getModel() must return a non-null object", model);
            assertEquals("Has myExample model present", EXPECTED_MY_EXAMPLE_MODEL, model);
        } finally {
            oeClient.deleteModel(EXPECTED_MY_EXAMPLE_MODEL);
        }
    }

    @Test
    public void getModel_fullURI() throws OEClientException {
        oeClient.createModel(EXPECTED_MY_EXAMPLE_MODEL);
        try {
            Model model = oeClient.getModel("urn:x-evn-master:myExample");
            assertNotNull("getModel() must return a non-null object", model);
            assertEquals("Has myExample model present", EXPECTED_MY_EXAMPLE_MODEL, model);
        } finally {
            oeClient.deleteModel(EXPECTED_MY_EXAMPLE_MODEL);
        }
    }

    @Test
    public void createAndDeleteModel() throws OEClientException {
        Model newModel = createUniqueModel();
        assertModelNotExists(newModel);
        oeClient.createModel(newModel);
        Model model = oeClient.getModel(newModel.getUri());
        assertNotNull("Expect new model exists", model);
        assertEquals("Expect new model is equal to created model", newModel, model);
        oeClient.deleteModel(newModel);
        assertModelNotExists(newModel);
    }

    @Test
    public void createConceptSchemes() throws OEClientException {
        Model model = createEmptyModel();
        oeClient.setModelUri(model.getUri());
        final List<ConceptScheme> expectedConceptSchemes = List.of(new ConceptScheme(oeClient, "example:scheme1", List.of(new Label(null, "Example Scheme 1"))));
        oeClient.createConceptSchemes(expectedConceptSchemes);
        List<ConceptScheme> allConceptSchemes = oeClient.getAllConceptSchemes().stream().toList();
        assertEquals(expectedConceptSchemes.size(), allConceptSchemes.size());
        assertEquals(expectedConceptSchemes.get(0), allConceptSchemes.get(0));
    }

    @After
    public void deleteModels() throws OEClientException {
        List<Model> modelsToDelete = oeClient.getAllModels().stream().filter(model -> model.getLabel().getValue().startsWith("OE_CLIENT_EXAMPLE_")).filter(model -> !model.getLabel().getValue().endsWith("_owl")).toList();
        for(Model model: modelsToDelete) {
            oeClient.deleteModel(model);
        }
    }

    private void assertModelNotExists(Model model) {
        try {
            oeClient.getModel(model.getUri());
        } catch (OEClientException e) {
            assertEquals("validation.notFound", e.getMessage().contains("validation.notFound") ? "validation.notFound" : e.getMessage());
        }

    }

    private Model createEmptyModel() throws OEClientException {
        Model uniqueModel = createUniqueModel();
        oeClient.createModel(uniqueModel);
        return uniqueModel;
    }

    private Model createUniqueModel() {
        final String randomUUID = UUID.randomUUID().toString();
        final String modelLabel = "OE_CLIENT_EXAMPLE_" + randomUUID;
        final String modelUri = "model:" + modelLabel;
        return new Model(modelUri, new Label("", modelLabel), null);
    }

}

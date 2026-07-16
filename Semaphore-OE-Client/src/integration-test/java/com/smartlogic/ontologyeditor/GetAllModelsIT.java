// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test – retrieves the list of models from the live OE server and
 * verifies that a non-null collection is returned.
 *
 * <p>Skipped automatically when {@code OE_BASE_URL} / {@code OE_MODEL_URI} env
 * vars are not set. See {@link AbstractModelScopedIT} for configuration.
 */
public class GetAllModelsIT extends AbstractModelScopedIT {

    @Test
    public void getAllModels_expectNewModelToBePresent() throws OEClientException {
        List<Model> models = oeClient.getAllModels().stream().toList();
        assertNotNull("getAllModels() must return a non-null collection", models);
        assertTrue("Expected models in the collection", !models.isEmpty());
        models.stream().filter(model -> model.getUri().equals(testModel.getUri())).findFirst().orElseThrow(() -> new AssertionError("Expected test model to be present in the collection"));
    }

    @Test
    public void getModel_shortURI() throws OEClientException {
        String shortUri = "model:" + testModel.getUri().substring(testModel.getUri().lastIndexOf(':') + 1);
        Model model = oeClient.getModel(shortUri);
        assertNotNull("getModel() must return a non-null object", model);
        assertEquals("Has test model present", testModel, model);
    }

    @Test
    public void getModel_fullURI() throws OEClientException {
        String fullUri = "urn:x-evn-master:" + testModel.getUri().substring(testModel.getUri().lastIndexOf(':') + 1);
        Model model = oeClient.getModel(fullUri);
        assertNotNull("getModel() must return a non-null object", model);
        assertEquals("Has myExample model present", testModel, model);
    }

    @Test
    public void deleteModel() throws OEClientException {
        oeClient.deleteModel(testModel);
        assertModelNotExists(testModel);
    }

    @Test
    public void createConceptSchemes() throws OEClientException {
        oeClient.setModelUri(testModel.getUri());
        final List<ConceptScheme> expectedConceptSchemes = List.of(new ConceptScheme(oeClient, "example:scheme1", List.of(new Label(null, "Example Scheme 1"))));
        oeClient.createConceptSchemes(expectedConceptSchemes);
        List<ConceptScheme> allConceptSchemes = oeClient.getAllConceptSchemes().stream().toList();
        assertEquals(expectedConceptSchemes.size(), allConceptSchemes.size());
        assertEquals(expectedConceptSchemes.get(0), allConceptSchemes.get(0));
    }

    private void assertModelNotExists(Model model) {
        try {
            oeClient.getModel(model.getUri());
        } catch (OEClientException e) {
            assertEquals("validation.notFound", e.getMessage().contains("validation.notFound") ? "validation.notFound" : e.getMessage());
        }

    }

    private Model createUniqueModel() {
        final String randomUUID = UUID.randomUUID().toString();
        final String modelLabel = "OE_CLIENT_EXAMPLE_" + randomUUID;
        final String modelUri = "model:" + modelLabel;
        return new Model(modelUri, new Label("", modelLabel), null);
    }

}

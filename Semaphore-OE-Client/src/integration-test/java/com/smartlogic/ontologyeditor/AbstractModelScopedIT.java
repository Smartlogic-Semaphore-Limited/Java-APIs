// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;
import org.junit.After;
import org.junit.Before;

import java.util.UUID;

/**
 * Base class for integration tests that require an isolated model.
 *
 * <p>Each test class that extends this class gets a fresh, uniquely-named model
 * created before the first test and automatically deleted after the last test.
 * The client's model URI is pointed at the new model, so tests are completely
 * isolated from {@code model:myExample} and from each other.
 *
 * <p>Model names follow the same {@code OE_CLIENT_EXAMPLE_} prefix convention
 * as {@link GetAllModelsIT} so that any stale models can be discovered and
 * cleaned up easily.
 */
public abstract class AbstractModelScopedIT extends AbstractOEIntegrationTest {

    protected Model testModel;

    /**
     * Creates a unique model and switches the client to use it.
     * Runs after {@link AbstractOEIntegrationTest#setUpClient()} because JUnit 4
     * always executes {@code @Before} methods in superclass-first order.
     */
    @Before
    public void createTestModel() throws OEClientException {
        if (oeClient == null) {
            // Parent setUp was skipped (env vars not set) – nothing to do.
            return;
        }
        String modelLabel = "OE_CLIENT_EXAMPLE_" + getClass().getCanonicalName() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String modelUri = "model:" + modelLabel;
        Model modelToCreate = new Model(modelUri, new Label("", modelLabel), null,
                "http://example.test/" + modelLabel + "#");
        oeClient.createModel(modelToCreate);
        oeClient.setModelUri(modelUri);
        // Re-fetch from the server so testModel.getDefaultNamespace() (and getComment())
        // reflect the real, server-assigned values instead of the value we supplied at creation.
        testModel = oeClient.getModel(modelUri);
    }

    /**
     * Deletes the unique model and all its contents after each test method.
     * JUnit 4 runs {@code @After} in subclass-first order, so this runs
     * after any {@code @After} cleanup in the concrete test class.
     */
    @After
    public void deleteTestModel() throws OEClientException {
        if (oeClient != null && testModel != null) {
            try {
                oeClient.deleteModel(testModel);
            } catch (Exception e) {
                // Log and continue – don't mask a test failure with a cleanup error
                System.err.println("Warning: failed to delete test model " + testModel.getUri() + ": " + e.getMessage());
            } finally {
                testModel = null;
            }
        }
    }
}


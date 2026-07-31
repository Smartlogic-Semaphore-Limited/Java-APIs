// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Model;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * Fetches the current values of a single property of the resource (model or task) at
     * {@code sysGraphUri} directly from the API, bypassing any bean mapping. Shared by IT tests
     * that need to assert on the raw server state of a model or task setting (e.g. {@code
     * rdfs:comment}, {@code swa:defaultNamespace}, {@code sem:color}, {@code sem:tag},
     * {@code sempermissions:*}) after a mutation, since {@link Model}/{@code Task} only expose a
     * subset of these as typed fields.
     *
     * @param sysGraphUri the model URI or task graph URI to query, e.g. {@code testModel.getUri()}
     *        or {@code testTask.getGraphUri()}
     * @param propertyUri the JSON-LD property to fetch, e.g. {@code "rdfs:comment"}
     * @return the property's current values (empty if the resource or property is absent)
     */
    protected List<String> getPropertyValues(String sysGraphUri, String propertyUri) throws OEClientException {
        Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put(OEClientReadOnly.PARAM_PROPERTIES, propertyUri);

        String url = oeClient.getApiURL() + "sys/" + sysGraphUri;
        String response = oeClient.getResponse(url, queryParameters);

        JsonObject jsonResponse = JSON.parse(response);
        JsonArray graph = jsonResponse.get(OEClientReadOnly.JSON_LD_GRAPH).getAsArray();

        List<String> values = new ArrayList<>();
        if (graph.size() == 0) {
            return values;
        }

        JsonObject resourceObject = graph.get(0).getAsObject();
        JsonValue propertyValue = resourceObject.get(propertyUri);
        if (propertyValue == null) {
            return values;
        }

        if (propertyValue.isArray()) {
            propertyValue.getAsArray().forEach(value -> values.add(extractValue(value)));
        } else {
            values.add(extractValue(propertyValue));
        }
        return values;
    }

    private String extractValue(JsonValue value) {
        if (value.isObject() && value.getAsObject().get("@value") != null) {
            return value.getAsObject().get("@value").getAsString().value();
        }
        if (value.isObject() && value.getAsObject().get("@id") != null) {
            return value.getAsObject().get("@id").getAsString().value();
        }
        if (value.isString()) {
            return value.getAsString().value();
        }
        return value.toString();
    }
}


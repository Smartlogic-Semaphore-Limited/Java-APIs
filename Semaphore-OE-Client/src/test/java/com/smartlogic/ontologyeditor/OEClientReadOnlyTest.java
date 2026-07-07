// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Model;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OEClientReadOnlyTest {

  @Test
  public void getConceptCountReturnsParsedCount() throws OEClientException {
    StubReadOnlyClient client = newClient();
    client.enqueueResponse("{\"@graph\":[{\"meta:meta\":{\"meta:localTransitiveInstance\":{\"meta:count\":42}}}]}");

    long count = client.getConceptCount();

    assertEquals(42L, count);
  }

  @Test
  public void getConceptCountReturnsZeroForEmptyGraph() throws OEClientException {
    StubReadOnlyClient client = newClient();
    client.enqueueResponse("{\"@graph\":[]}");

    long count = client.getConceptCount();

    assertEquals(0L, count);
  }

  @Test
  public void getConceptCountThrowsForMalformedPayload() throws OEClientException {
    StubReadOnlyClient client = newClient();
    client.enqueueResponse("{\"@graph\":[{\"meta:meta\":{}}]}");

    try {
      client.getConceptCount();
      fail("Expected OEClientException for malformed concept count payload");
    } catch (OEClientException ex) {
      assertTrue(ex.getMessage().contains("Failed to parse concept count response"));
    }
  }

  @Test
  public void getModelReturnsMappedModelWhenPresent() throws OEClientException {
    StubReadOnlyClient client = newClient();
    client.enqueueResponse("{\"@graph\":[{\"meta:displayName\":{\"@value\":\"My Model\"},\"meta:graphUri\":{\"@id\":\"model:my\"}}]}");

    Model model = client.getModel("model:my");

    assertEquals("model:my", model.getUri());
    assertEquals("My Model", model.getLabel().getValue());
  }

  @Test
  public void getModelThrowsWhenGraphEmpty() throws OEClientException {
    StubReadOnlyClient client = newClient();
    client.enqueueResponse("{\"@graph\":[]}");

    try {
      client.getModel("missing:model");
      fail("Expected OEClientException for missing model");
    } catch (OEClientException ex) {
      assertTrue(ex.getMessage().contains("Model not found: missing:model"));
    }
  }

  private static StubReadOnlyClient newClient() {
    StubReadOnlyClient client = new StubReadOnlyClient();
    client.setBaseURL("http://localhost");
    client.setModelUri("model:test");
    return client;
  }

  private static class StubReadOnlyClient extends OEClientReadOnly {
    private final Queue<String> responses = new ArrayDeque<>();

    private void enqueueResponse(String response) {
      responses.add(response);
    }

    @Override
    protected String getResponse(String url, Map<String, String> queryParameters) {
      return responses.remove();
    }
  }
}


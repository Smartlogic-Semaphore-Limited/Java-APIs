// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Model;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

  @Test
  public void transactionMessageHeaderIsAppliedAndConsumed() {
    StubReadOnlyClient client = newClient();
    client.setTransactionMessage("New concept Created");

    HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://localhost"));
    client.applyTransactionMessageHeaders(builder);
    HttpRequest request = builder.build();

    assertEquals("New concept Created", request.headers().firstValue("X-Transaction-Message").orElse(null));
    assertFalse(request.headers().firstValue("X-Transaction-Message-Json").isPresent());

    // one-shot: applying again should not resend the message
    HttpRequest.Builder secondBuilder = HttpRequest.newBuilder().uri(URI.create("http://localhost"));
    client.applyTransactionMessageHeaders(secondBuilder);
    assertFalse(secondBuilder.build().headers().firstValue("X-Transaction-Message").isPresent());
  }

  @Test
  public void transactionMessageHeaderAppendsOperationSourceSuffix() {
    StubReadOnlyClient client = newClient();
    client.setOperationSource("KMM AI Assistant");
    client.setTransactionMessage("New concept Created");

    HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://localhost"));
    client.applyTransactionMessageHeaders(builder);

    assertEquals("New concept Created (via KMM AI Assistant)",
            builder.build().headers().firstValue("X-Transaction-Message").orElse(null));
  }

  @Test
  public void structuredTransactionMessageIsSerializedAsJson() {
    StubReadOnlyClient client = newClient();
    client.setTransactionMessage("concept-multiple-added", Arrays.asList(1, "http://example.com/demo-model#New-Concept"));

    HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://localhost"));
    client.applyTransactionMessageHeaders(builder);
    HttpRequest request = builder.build();

    String jsonHeader = request.headers().firstValue("X-Transaction-Message-Json").orElse(null);
    assertTrue(jsonHeader.contains("\"templateKey\":\"concept-multiple-added\""));
    assertTrue(jsonHeader.contains("http://example.com/demo-model#New-Concept"));
    assertFalse(request.headers().firstValue("X-Transaction-Message").isPresent());
  }

  @Test
  public void clearTransactionMessageRemovesPendingMessage() {
    StubReadOnlyClient client = newClient();
    client.setTransactionMessage("Should not be sent");
    client.clearTransactionMessage();

    HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://localhost"));
    client.applyTransactionMessageHeaders(builder);

    assertFalse(builder.build().headers().firstValue("X-Transaction-Message").isPresent());
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


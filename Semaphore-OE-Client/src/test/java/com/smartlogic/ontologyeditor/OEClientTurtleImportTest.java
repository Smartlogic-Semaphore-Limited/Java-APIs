// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OEClientTurtleImportTest {

  private HttpServer server;
  private OEClientReadWrite client;
  private final AtomicReference<CapturedRequest> capturedRequest = new AtomicReference<>();
  private volatile int responseStatus;
  private volatile String responseBody;

  @Before
  public void setUp() throws IOException {
    responseStatus = 204;
    responseBody = "";
    server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
    server.createContext("/api", this::handleRequest);
    server.start();

    client = new OEClientReadWrite();
    client.setBaseURL("http://localhost:" + server.getAddress().getPort());
    client.setHeaderToken("test-api-key");
    client.setHeader("X-Test-Header", "test-value");
  }

  @After
  public void tearDown() {
    server.stop(0);
  }

  @Test
  public void importTurtlePostsEncodedModelTargetAndMultipartContent() throws Exception {
    String turtle = "@prefix ex: <https://example.test/> .\nex:subject ex:label \"Café\" .";
    client.setTransactionMessage("Import Turtle");

    client.importTurtle("model:project:123", turtle);

    CapturedRequest request = capturedRequest.get();
    assertEquals("POST", request.method);
    assertEquals(
        "path=backup%2Fmodel%3Aproject%3A123%2Fimport&checkConstraints=true",
        request.rawQuery);
    assertEquals("test-api-key", request.apiKey);
    assertEquals("test-value", request.testHeader);
    assertEquals("Import Turtle", request.transactionMessage);
    assertEquals("application/ld+json,application/json", request.accept);
    assertTrue(request.contentType.startsWith("multipart/form-data; boundary="));

    assertTrue(request.body.contains("name=\"file\"; filename=\"import.ttl\""));
    assertTrue(request.body.contains("Content-Type: text/turtle"));
    assertTrue(request.body.contains(turtle));
    assertMultipartField(request.body, "format", "text/turtle");
    assertMultipartField(request.body, "overwrite", "false");
    assertMultipartField(request.body, "record", "true");
  }

  @Test
  public void importTurtleEncodesTaskTargetUri() throws Exception {
    client.importTurtle("task:project:456", "<urn:s> <urn:p> <urn:o> .");

    assertEquals(
        "path=backup%2Ftask%3Aproject%3A456%2Fimport&checkConstraints=true",
        capturedRequest.get().rawQuery);
  }

  @Test
  public void importTurtleWithCheckConstraintsFalseOmitsQueryParameter() throws Exception {
    client.importTurtle("model:project:123", "<urn:s> <urn:p> <urn:o> .", false);

    assertEquals(
        "path=backup%2Fmodel%3Aproject%3A123%2Fimport",
        capturedRequest.get().rawQuery);
  }

  @Test
  public void importTurtleSurfacesConstraintViolationAsOEClientException() {
    responseStatus = 409;
    responseBody = "{\"errors\":[{\"message\":\"Missing required property\"}]}";

    try {
      client.importTurtle("model:project:123", "<urn:s> <urn:p> <urn:o> .");
      fail("Expected OEClientException");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("409"));
      assertTrue(e.getMessage().contains("Missing required property"));
    }
  }

  @Test
  public void importTurtleTurnsErrorResponseIntoOEClientException() {
    responseStatus = 400;
    responseBody = "Invalid Turtle";

    try {
      client.importTurtle("model:project:123", "not turtle");
      fail("Expected OEClientException");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("400"));
      assertTrue(e.getMessage().contains("Invalid Turtle"));
    }
  }

  @Test
  public void validateSpinConstraintsGetsEncodedModelTargetAndWarningDetails() throws Exception {
    responseStatus = 200;
    responseBody =
        "{\"warnings\":[{\"constraintId\":\"N001\",\"message\":\"Missing label\"},\"Plain warning\"]}";

    List<String> warnings = client.validateSpinConstraints("model:project:123");

    assertEquals(2, warnings.size());
    assertTrue(warnings.get(0).contains("\"constraintId\":\"N001\""));
    assertTrue(warnings.get(0).contains("\"message\":\"Missing label\""));
    assertEquals("Plain warning", warnings.get(1));
    CapturedRequest request = capturedRequest.get();
    assertEquals("GET", request.method);
    assertEquals(
        "path=special%2FvalidateSpinConstraints&graphUri=model%3Aproject%3A123",
        request.rawQuery);
    assertEquals("test-api-key", request.apiKey);
    assertEquals("test-value", request.testHeader);
    assertEquals("application/ld+json,application/json", request.accept);
  }

  @Test
  public void validateSpinConstraintsEncodesTaskTargetUriAndReturnsEmptyHealthyResult() throws Exception {
    responseStatus = 200;
    responseBody = "{\"warnings\":[]}";

    List<String> warnings = client.validateSpinConstraints("task:project:456");

    assertTrue(warnings.isEmpty());
    assertEquals(
        "path=special%2FvalidateSpinConstraints&graphUri=task%3Aproject%3A456",
        capturedRequest.get().rawQuery);
  }

  @Test
  public void validateSpinConstraintsTurnsErrorsIntoOEClientException() {
    responseStatus = 500;
    responseBody = "Constraint service unavailable";

    try {
      client.validateSpinConstraints("model:project:123");
      fail("Expected OEClientException");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("500"));
      assertTrue(e.getMessage().contains("Constraint service unavailable"));
    }
  }

  @Test
  public void validateSpinConstraintsRejectsMissingWarnings() {
    responseStatus = 200;
    responseBody = "{}";

    try {
      client.validateSpinConstraints("model:project:123");
      fail("Expected OEClientException");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("warnings array is missing or null"));
    }
  }

  @Test
  public void validateSpinConstraintsRejectsNullWarnings() {
    responseStatus = 200;
    responseBody = "{\"warnings\":null}";

    try {
      client.validateSpinConstraints("model:project:123");
      fail("Expected OEClientException");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("warnings array is missing or null"));
    }
  }

  private void handleRequest(HttpExchange exchange) throws IOException {
    capturedRequest.set(new CapturedRequest(
        exchange.getRequestMethod(),
        exchange.getRequestURI().getRawQuery(),
        exchange.getRequestHeaders().getFirst("Content-Type"),
        exchange.getRequestHeaders().getFirst("Accept"),
        exchange.getRequestHeaders().getFirst("X-Api-Key"),
        exchange.getRequestHeaders().getFirst("X-Test-Header"),
        exchange.getRequestHeaders().getFirst("X-Transaction-Message"),
        new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8)));
    byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(responseStatus, body.length);
    exchange.getResponseBody().write(body);
    exchange.close();
  }

  private static void assertMultipartField(String body, String name, String value) {
    assertTrue(body.contains(
        "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" + value + "\r\n"));
  }

  private static class CapturedRequest {
    private final String method;
    private final String rawQuery;
    private final String contentType;
    private final String accept;
    private final String apiKey;
    private final String testHeader;
    private final String transactionMessage;
    private final String body;

    private CapturedRequest(
        String method,
        String rawQuery,
        String contentType,
        String accept,
        String apiKey,
        String testHeader,
        String transactionMessage,
        String body) {
      this.method = method;
      this.rawQuery = rawQuery;
      this.contentType = contentType;
      this.accept = accept;
      this.apiKey = apiKey;
      this.testHeader = testHeader;
      this.transactionMessage = transactionMessage;
      this.body = body;
    }
  }
}

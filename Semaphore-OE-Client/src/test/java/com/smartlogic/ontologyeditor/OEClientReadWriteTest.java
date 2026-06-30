// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.google.gson.JsonParser;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;
import com.smartlogic.ontologyeditor.beans.Task;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.Test;

import java.net.URI;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OEClientReadWriteTest {

  @Test
  public void createConceptsEmptyListSkipsRequest() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    client.createConcepts(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    assertEquals("No request should be sent for empty input", 0, client.makeRequestCallCount);
  }

  @Test
  public void createConceptsBelowConceptNullConceptsThrows() {
    CapturingReadWriteClient client = newClient();

    try {
      client.createConceptsBelowConcept("urn:concept:parent", null, Collections.emptyList());
      fail("Expected OEClientException for null concepts");
    } catch (OEClientException ex) {
      assertTrue(ex.getMessage().contains("concepts"));
      assertEquals(0, client.makeRequestCallCount);
    }
  }

  @Test
  public void createConceptsBelowConceptSendsRequest() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:new", List.of(new Label("en", "Child")));

    client.createConceptsBelowConcept("urn:concept:parent", List.of(concept), List.of(Collections.emptyMap()));

    assertEquals(1, client.makeRequestCallCount);
  }

  @Test
  public void createConceptsBuildsExpectedGraphPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    Concept topConcept = new Concept(client, "urn:concept:top", List.of(new Label("en", "Top")));
    topConcept.setGuid("guid-top");
    topConcept.addClass("ex:CustomClass");
    topConcept.addAltLabel("skosxl:altLabel", new Label("en", "Top Alt"));
    topConcept.addRelationship("skos:related", "urn:concept:target");
    topConcept.addRelationship("skos:broader", "urn:concept:should-not-be-copied");

    Concept childConcept = new Concept(client, "urn:concept:child", List.of(new Label("en", "Child")));

    Map<String, Collection<MetadataValue>> metadataTop = new HashMap<>();
    metadataTop.put("ex:metaTop", List.of(new MetadataValue("en", "v-top")));

    Map<String, Collection<MetadataValue>> metadataChild = new HashMap<>();
    metadataChild.put("ex:metaChild", List.of(new MetadataValue("en", "v-child")));

    client.createConcepts(
        Arrays.asList(topConcept, childConcept),
        Arrays.asList("urn:scheme:one", "urn:concept:parent"),
        Arrays.asList(true, false),
        Arrays.asList(metadataTop, metadataChild));

    assertEquals(1, client.makeRequestCallCount);
    assertNotNull(client.lastPayload);

    JsonObject payload = JSON.parse(client.lastPayload);
    JsonArray graph = payload.get("@graph").getAsArray();
    assertEquals(2, graph.size());

    JsonObject top = graph.get(0).getAsObject();
    assertNotNull(top.get("skos:topConceptOf"));
    assertNull(top.get("skos:broader"));
    assertNotNull(top.get("skosxl:altLabel"));
    assertNotNull(top.get("skos:related"));
    assertNotNull(top.get("ex:metaTop"));
    assertNull(top.get("ex:metaChild"));

    JsonObject child = graph.get(1).getAsObject();
    assertNotNull(child.get("skos:broader"));
    assertNull(child.get("skos:topConceptOf"));
    assertNotNull(child.get("ex:metaChild"));
    assertNull(child.get("ex:metaTop"));
  }

  @Test
  public void createLabelsUsesPerConceptRelationshipType() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    client.createLabels(
        new String[] {"urn:concept:1", "urn:concept:2"},
        new String[] {"skosxl:prefLabel", "ex:altPreferred"},
        new Label[] {new Label("en", "Label1"), new Label("en", "Label2")});

    JsonObject payload = JSON.parse(client.lastPayload);
    JsonArray graph = payload.get("@graph").getAsArray();

    JsonObject first = graph.get(0).getAsObject();
    JsonObject second = graph.get(1).getAsObject();

    assertNotNull(first.get("skosxl:prefLabel"));
    assertNull(first.get("ex:altPreferred"));

    assertNotNull(second.get("ex:altPreferred"));
    assertNull(second.get("skosxl:prefLabel"));
  }

  @Test
  public void createLabelsRejectsMismatchedArrayLengths() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    try {
      client.createLabels(
          new String[] {"urn:concept:1"},
          new String[] {"skosxl:prefLabel", "ex:altPreferred"},
          new Label[] {new Label("en", "Label1")});
      fail("Expected IllegalArgumentException for mismatched array lengths");
    } catch (IllegalArgumentException ex) {
      assertTrue(ex.getMessage().contains("relationshipTypeUris size"));
    }
  }

  @Test
  public void updateTypedDecimalMetadataUsesXsdDecimal() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.updateMetadata(concept, "ex:score", 12.3400d, 100.0d);

    assertEquals(1, client.makeRequestCallCount);
    assertTrue(client.lastPayload.contains("xsd:decimal"));
    assertTrue(client.lastPayload.contains("12.34"));
    assertTrue(client.lastPayload.contains("100"));
  }

  @Test
  public void deleteTypedIntegerMetadataUsesXsdInteger() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.deleteMetadata(concept, "ex:rank", 7);

    assertEquals(1, client.makeRequestCallCount);
    assertTrue(client.lastPayload.contains("xsd:integer"));
    assertTrue(client.lastPayload.contains("\"@value\" : 7"));
    assertFalse(client.lastPayload.isEmpty());
  }

  @Test
  public void deleteTypedUriMetadataUsesAnyUriType() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.deleteMetadata(concept, "ex:link", URI.create("https://example.test/value"));

    assertTrue(client.lastPayload.contains("xsd:anyURI"));
    assertTrue(client.lastPayload.contains("https://example.test/value"));
  }

  @Test
  public void createTaskAndReturnUsesResolvedTaskFromServer() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    client.nextMakeRequestResponse = "urn:task:id:123";
    client.tasksResponse = List.of(new Task(new Label("en", "Task A"), "urn:task:id:123", "urn:task:graph:123"));

    Task createdTask = client.createTaskAndReturn(new Task(new Label("en", "Task A")));

    assertNotNull(createdTask);
    assertEquals("urn:task:id:123", createdTask.getId());
    assertEquals("urn:task:graph:123", createdTask.getGraphUri());
  }

  @Test
  public void createConceptSchemeAndReturnUsesServerUriWhenPresent() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    client.nextMakeRequestResponse = "urn:scheme:new";

    ConceptScheme createdScheme = client.createConceptSchemeAndReturn(
        new ConceptScheme(client, "urn:scheme:input", List.of(new Label("en", "Scheme Label"))));

    assertNotNull(createdScheme);
    assertEquals("urn:scheme:new", createdScheme.getUri());
    assertEquals(1, createdScheme.getPrefLabels().size());
  }

  @Test
  public void createConceptSchemesSendsGraphPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    client.createConceptSchemes(List.of(
        new ConceptScheme(client, "urn:scheme:one", List.of(new Label("en", "One"))),
        new ConceptScheme(client, "urn:scheme:two", List.of(new Label("en", "Two")))));

    assertEquals(1, client.makeRequestCallCount);
    JsonObject payload = JSON.parse(client.lastPayload);
    JsonArray graph = payload.get("@graph").getAsArray();
    assertEquals(2, graph.size());
    assertEquals("skos:ConceptScheme", graph.get(0).getAsObject().get("@type").getAsArray().get(0).getAsString().value());
    assertEquals("skos:ConceptScheme", graph.get(1).getAsObject().get("@type").getAsArray().get(0).getAsString().value());
  }

  @Test
  public void createConceptSchemesEmptyListReturnsNull() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    String created = client.createConceptSchemes(Collections.emptyList());

    assertNull(created);
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void createLabelStringVariantBuildsCorrectPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    client.createLabel("urn:concept:1", "skosxl:prefLabel", new Label("en", "Hello"));

    assertEquals(1, client.makeRequestCallCount);
    assertTrue(client.lastPayload.contains("urn:concept:1"));
    assertTrue(client.lastPayload.contains("skosxl:prefLabel"));
  }

  @Test
  public void createLabelConceptVariantDelegatesToStringVariant() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.createLabel(concept, "skosxl:prefLabel", new Label("en", "Hello"));

    assertEquals(1, client.makeRequestCallCount);
  }

  @Test
  public void createMetadataStringBuildsLanguagePayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.createMetadata(concept, "ex:note", "hello", "en");

    assertEquals(1, client.makeRequestCallCount);
    com.google.gson.JsonArray payload = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    com.google.gson.JsonObject value = payload.get(1).getAsJsonObject().getAsJsonArray("value").get(0).getAsJsonObject();
    assertEquals("hello", value.get("@value").getAsString());
    assertEquals("en", value.get("@language").getAsString());
  }

  @Test
  public void createMetadataUriBuildsAnyUriPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.createMetadata(concept, "ex:link", URI.create("https://test.example"));

    assertTrue(client.lastPayload.contains("xsd:anyURI"));
    assertTrue(client.lastPayload.contains("https://test.example"));
  }

  @Test
  public void createMetadataIntBuildsIntegerPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.createMetadata(concept, "ex:rank", 5);

    assertTrue(client.lastPayload.contains("xsd:integer"));
    assertTrue(client.lastPayload.contains("5"));
  }

  @Test
  public void createMetadataDoubleBuildsDecimalPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.createMetadata(concept, "ex:score", 3.14d);

    assertTrue(client.lastPayload.contains("xsd:decimal"));
    assertTrue(client.lastPayload.contains("3.14"));
  }

  @Test
  public void createMetadataBooleanBuildsBooleanPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.createMetadata(concept, "ex:flag", true);

    assertTrue(client.lastPayload.contains("xsd:boolean"));
  }

  @Test
  public void addLanguageBlankLanguageThrows() {
    CapturingReadWriteClient client = newClient();

    try {
      client.addLanguage("", "en");
      fail("Expected OEClientException for blank language");
    } catch (OEClientException ex) {
      assertTrue(ex.getMessage().contains("language must not be blank"));
      assertEquals(0, client.makeRequestCallCount);
    }
  }

  @Test
  public void addLanguageBlankNotationThrows() {
    CapturingReadWriteClient client = newClient();

    try {
      client.addLanguage("English", "");
      fail("Expected OEClientException for blank notation");
    } catch (OEClientException ex) {
      assertTrue(ex.getMessage().contains("notation must not be blank"));
      assertEquals(0, client.makeRequestCallCount);
    }
  }

  @Test
  public void updateMetadataDateBuildsXsdDatePayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.updateMetadata(concept, "ex:date", new Date(0L), new Date(1000L));

    assertEquals(1, client.makeRequestCallCount);
    assertTrue(client.lastPayload.contains("xsd:date"));
  }

  @Test
  public void updateMetadataUriBuildsAnyUriPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.updateMetadata(concept, "ex:link", URI.create("http://old.test"), URI.create("http://new.test"));

    assertEquals(1, client.makeRequestCallCount);
    assertTrue(client.lastPayload.contains("xsd:anyURI"));
  }

  @Test
  public void deleteMetadataDoubleBuildsDecimalPayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.deleteMetadata(concept, "ex:score", 9.5d);

    assertEquals(1, client.makeRequestCallCount);
    assertTrue(client.lastPayload.contains("xsd:decimal"));
  }

  @Test
  public void deleteMetadataDateBuildsXsdDatePayload() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Concept One")));

    client.deleteMetadata(concept, "ex:date", new Date(0L));

    assertEquals(1, client.makeRequestCallCount);
    assertTrue(client.lastPayload.contains("xsd:date"));
  }

  private static CapturingReadWriteClient newClient() {
    CapturingReadWriteClient client = new CapturingReadWriteClient();
    client.setBaseURL("http://localhost");
    client.setModelUri("model:test");
    return client;
  }

  private static class CapturingReadWriteClient extends OEClientReadWrite {
    private String lastPayload;
    private int makeRequestCallCount;
    private String nextMakeRequestResponse;
    private Collection<Task> tasksResponse = Collections.emptyList();

    @Override
    protected String makeRequest(String url, String payload, RequestType requestType) {
      this.lastPayload = payload;
      this.makeRequestCallCount++;
      return nextMakeRequestResponse;
    }

    @Override
    protected String makeRequest(String url, Map<String, String> queryParameters, String payload, RequestType requestType) {
      this.lastPayload = payload;
      this.makeRequestCallCount++;
      return nextMakeRequestResponse;
    }

    @Override
    public Collection<Task> getAllTasks() {
      return tasksResponse;
    }

    @Override
    protected String getResponse(String url, Map<String, String> queryParameters) {
      return "{\"@graph\":[]}";
    }
  }
}




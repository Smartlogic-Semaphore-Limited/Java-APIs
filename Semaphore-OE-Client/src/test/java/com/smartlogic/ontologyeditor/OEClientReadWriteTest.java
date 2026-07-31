// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.google.gson.JsonParser;
import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;
import com.smartlogic.ontologyeditor.beans.Model;
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
  public void createTaskAndReturnThrowsWhenServerReturnsNoUri() {
    CapturingReadWriteClient client = newClient();
    client.nextMakeRequestResponse = null;

    try {
      client.createTaskAndReturn(new Task(new Label("en", "Task A")));
      fail("Expected OEClientException when server returns no URI");
    } catch (OEClientException ex) {
      assertTrue(ex.getMessage().contains("did not return a URI"));
    }
  }

  @Test
  public void deleteTaskSendsDeleteRequestToTaskSysUrl() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task A"), "urn:task:id:123", "task:test:taskA");

    client.deleteTask(task);

    assertEquals(1, client.makeRequestCallCount);
    assertEquals(OEClientReadOnly.RequestType.DELETE, client.lastRequestType);
    assertEquals("http://localhost/api/sys/task:test:taskA", client.lastUrl);
  }

  @Test
  public void commitTaskCommitsAllUncommittedChangesWhenNoDateSupplied() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task A"), "urn:task:id:123", "task:test:taskA");

    client.commitTask(task, new Label("en", "My commit"), "My comment");

    assertEquals(1, client.makeRequestCallCount);
    assertEquals("http://localhost/api/sys/task:test:taskA/teamwork:Change/rdf:instance", client.lastUrl);
    assertNotNull(client.lastQueryParameters);
    assertEquals("commit", client.lastQueryParameters.get("action"));
    assertEquals("true", client.lastQueryParameters.get("checkConstraints"));
    assertEquals("subject(teamwork:status = teamwork:Uncommitted)",
        client.lastQueryParameters.get("filters"));
    assertEquals("not exists { ?subject sem:accepted false }",
        client.lastQueryParameters.get("sparqlFilter"));
    assertEquals("en", client.lastQueryParameters.get("language"));
  }

  @Test
  public void commitTaskUsesLabelLanguageForCommentLanguage() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task A"), "urn:task:id:123", "task:test:taskA");

    client.commitTask(task, new Label("en", "My commit"), "My comment");

    JsonObject payload = JSON.parse(client.lastPayload);
    JsonObject commentObject =
        payload.get("@graph").getAsObject().get("rdfs:comment").getAsArray().get(0).getAsObject();
    assertEquals("en", commentObject.get("@language").getAsString().value());
    assertEquals("My comment", commentObject.get("@value").getAsString().value());
  }

  @Test
  public void commitTaskUpToDateAddsCutoffFilterAheadOfStatusFilter() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task A"), "urn:task:id:123", "task:test:taskA");
    Date cutoff = new Date(1784876604438L);

    client.commitTask(task, new Label("en", "My commit"), "My comment", cutoff);

    assertEquals(
        "subject(dcterms:created <= \"" + cutoff.toInstant() + "\"^^xsd:dateTime),"
            + "subject(teamwork:status = teamwork:Uncommitted)",
        client.lastQueryParameters.get("filters"));
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
    JsonObject payload = JSON.parse(client.lastPayload);
    assertEquals("urn:concept:1", payload.get("@id").getAsString().value());
    JsonObject labelObj = payload.get("skosxl:prefLabel").getAsObject();
    assertEquals("skosxl:Label", labelObj.get("@type").getAsString().value());
    JsonArray literalForms = labelObj.get("skosxl:literalForm").getAsArray();
    assertEquals(1, literalForms.size());
    assertEquals("Hello", literalForms.get(0).getAsObject().get("@value").getAsString().value());
    assertEquals("en", literalForms.get(0).getAsObject().get("@language").getAsString().value());
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

  @Test
  public void createConceptWithCustomClassAlwaysIncludesSkosConceptType() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Concept concept = new Concept(client, "urn:concept:1", List.of(new Label("en", "Custom")));
    concept.addClass("ex:CustomClass");

    client.createConcepts(
        List.of(concept),
        List.of("urn:scheme:1"),
        List.of(true),
        List.of(Collections.emptyMap()));

    JsonObject payload = JSON.parse(client.lastPayload);
    String typesStr = payload.get("@graph").getAsArray().get(0).getAsObject().get("@type").getAsArray().toString();
    assertTrue(typesStr.contains("skos:Concept"));
    assertTrue(typesStr.contains("ex:CustomClass"));
  }

  @Test
  public void updateModelPatchTestOperationUsesValueKey() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Old Name"), null);

    client.updateModel(model, "New Name");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    com.google.gson.JsonObject testOp = patch.get(0).getAsJsonObject();
    assertEquals("test", testOp.get("op").getAsString());
    assertTrue(testOp.has("value"));
    assertFalse(testOp.has("@value"));
    assertEquals("Old Name", testOp.getAsJsonObject("value").get("@value").getAsString());
  }

  @Test
  public void addLanguagePayloadUsesAppendPath() throws OEClientException {
    CapturingReadWriteClient client = newClient();

    client.addLanguage("English", "en");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    assertEquals("@graph/0/dcterms:language/-", patch.get(0).getAsJsonObject().get("path").getAsString());
  }

  @Test
  public void assignModelRolePayloadAddsPrincipalByReference() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    client.assignModelRole(model, "manager", "user:jsmith");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    com.google.gson.JsonObject addOp = patch.get(0).getAsJsonObject();
    assertEquals("add", addOp.get("op").getAsString());
    assertEquals("@graph/0/sempermissions:manager/-", addOp.get("path").getAsString());
    assertEquals("user:jsmith", addOp.getAsJsonObject("value").get("@id").getAsString());
  }

  @Test
  public void assignModelRoleNormalizesRoleCase() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    client.assignModelRole(model, "Editor", "role:SemaphoreUsers");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    assertEquals("@graph/0/sempermissions:editor/-",
        patch.get(0).getAsJsonObject().get("path").getAsString());
  }

  @Test
  public void assignModelRoleRejectsUnknownRole() {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    try {
      client.assignModelRole(model, "owner", "user:jsmith");
      fail("Expected OEClientException for unknown role");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("owner"));
    }
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void unassignModelRolePayloadTestsAndRemovesPrincipal() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    client.unassignModelRole(model, "viewer", "user:jsmith");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    com.google.gson.JsonObject testOp = patch.get(0).getAsJsonObject();
    assertEquals("test", testOp.get("op").getAsString());
    assertEquals("@graph/0/sempermissions:viewer/0", testOp.get("path").getAsString());
    assertEquals("user:jsmith", testOp.getAsJsonObject("value").get("@id").getAsString());

    com.google.gson.JsonObject removeOp = patch.get(1).getAsJsonObject();
    assertEquals("remove", removeOp.get("op").getAsString());
    assertEquals("@graph/0/sempermissions:viewer/0", removeOp.get("path").getAsString());
  }

  @Test
  public void unassignModelRoleRejectsUnknownRole() {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    try {
      client.unassignModelRole(model, "bogus", "user:jsmith");
      fail("Expected OEClientException for unknown role");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("bogus"));
    }
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void assignModelRoleRejectsBlankPrincipalUri() {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    try {
      client.assignModelRole(model, "manager", " ");
      fail("Expected OEClientException for blank principalUri");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("principalUri"));
    }
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void unassignModelRoleRejectsNullPrincipalUri() {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    try {
      client.unassignModelRole(model, "manager", null);
      fail("Expected OEClientException for null principalUri");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("principalUri"));
    }
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void addModelTagRejectsBlankTag() {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    try {
      client.addModelTag(model, " ");
      fail("Expected OEClientException for blank tag");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("tag"));
    }
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void removeModelTagRejectsNullTag() {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    try {
      client.removeModelTag(model, null);
      fail("Expected OEClientException for null tag");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("tag"));
    }
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void updateTaskLabelPatchTestOperationUsesValueKey() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Old Task Name"), "task:1", "task:fp1:task1");

    client.updateTaskLabel(task, "Old Task Name", "New Task Name");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    com.google.gson.JsonObject testOp = patch.get(0).getAsJsonObject();
    assertEquals("test", testOp.get("op").getAsString());
    assertEquals("Old Task Name", testOp.getAsJsonObject("value").get("@value").getAsString());
    com.google.gson.JsonObject addOp = patch.get(2).getAsJsonObject();
    assertEquals("New Task Name", addOp.getAsJsonObject("value").get("@value").getAsString());
    assertTrue(client.lastUrl.endsWith("sys/task:fp1:task1"));
  }

  @Test
  public void updateTaskCommentAddsWhenNoOldComment() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task One"), "task:1", "task:fp1:task1");

    client.updateTaskComment(task, null, "New description");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    assertEquals(1, patch.size());
    com.google.gson.JsonObject addOp = patch.get(0).getAsJsonObject();
    assertEquals("add", addOp.get("op").getAsString());
    assertEquals("@graph/0/rdfs:comment/-", addOp.get("path").getAsString());
    assertEquals("New description", addOp.getAsJsonObject("value").get("@value").getAsString());
  }

  @Test
  public void updateModelDefaultNamespaceAddsWhenNoOldNamespace() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    client.updateModelDefaultNamespace(model, null, "http://example.com/model-one#");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    assertEquals(1, patch.size());
    com.google.gson.JsonObject addOp = patch.get(0).getAsJsonObject();
    assertEquals("add", addOp.get("op").getAsString());
    assertEquals("@graph/0/swa:defaultNamespace/-", addOp.get("path").getAsString());
    assertEquals("http://example.com/model-one#",
        addOp.getAsJsonObject("value").get("@value").getAsString());
  }

  @Test
  public void updateModelDefaultNamespaceTestsAndRemovesWhenOldNamespacePresent()
      throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Model model = new Model("urn:model:1", new Label("en", "Model One"), null);

    client.updateModelDefaultNamespace(model, "http://example.com/old#",
        "http://example.com/new#");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    assertEquals(3, patch.size());
    com.google.gson.JsonObject testOp = patch.get(0).getAsJsonObject();
    assertEquals("test", testOp.get("op").getAsString());
    assertEquals("http://example.com/old#",
        testOp.getAsJsonObject("value").get("@value").getAsString());
    com.google.gson.JsonObject removeOp = patch.get(1).getAsJsonObject();
    assertEquals("remove", removeOp.get("op").getAsString());
  }

  @Test
  public void addAndRemoveTaskTagUseSharedTagPaths() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task One"), "task:1", "task:fp1:task1");

    client.addTaskTag(task, "urgent");
    com.google.gson.JsonArray addPatch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    assertEquals("@graph/0/sem:tag/-", addPatch.get(0).getAsJsonObject().get("path").getAsString());

    client.removeTaskTag(task, "urgent");
    com.google.gson.JsonArray removePatch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    assertEquals("test", removePatch.get(0).getAsJsonObject().get("op").getAsString());
    assertEquals("@graph/0/sem:tag/0", removePatch.get(1).getAsJsonObject().get("path").getAsString());
  }

  @Test
  public void assignTaskRolePayloadAddsPrincipalByReference() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task One"), "task:1", "task:fp1:task1");

    client.assignTaskRole(task, "manager", "user:jsmith");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    com.google.gson.JsonObject addOp = patch.get(0).getAsJsonObject();
    assertEquals("add", addOp.get("op").getAsString());
    assertEquals("@graph/0/sempermissions:manager/-", addOp.get("path").getAsString());
    assertEquals("user:jsmith", addOp.getAsJsonObject("value").get("@id").getAsString());
    assertTrue(client.lastUrl.endsWith("sys/task:fp1:task1"));
  }

  @Test
  public void assignTaskRoleRejectsUnknownRole() {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task One"), "task:1", "task:fp1:task1");

    try {
      client.assignTaskRole(task, "owner", "user:jsmith");
      fail("Expected OEClientException for unknown role");
    } catch (OEClientException e) {
      assertTrue(e.getMessage().contains("owner"));
    }
    assertEquals(0, client.makeRequestCallCount);
  }

  @Test
  public void unassignTaskRolePayloadTestsAndRemovesPrincipal() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Task task = new Task(new Label("en", "Task One"), "task:1", "task:fp1:task1");

    client.unassignTaskRole(task, "viewer", "user:jsmith");

    com.google.gson.JsonArray patch = JsonParser.parseString(client.lastPayload).getAsJsonArray();
    com.google.gson.JsonObject testOp = patch.get(0).getAsJsonObject();
    assertEquals("test", testOp.get("op").getAsString());
    assertEquals("@graph/0/sempermissions:viewer/0", testOp.get("path").getAsString());
    assertEquals("user:jsmith", testOp.getAsJsonObject("value").get("@id").getAsString());

    com.google.gson.JsonObject removeOp = patch.get(1).getAsJsonObject();
    assertEquals("remove", removeOp.get("op").getAsString());
    assertEquals("@graph/0/sempermissions:viewer/0", removeOp.get("path").getAsString());
  }

  @Test
  public void createSymmetricRelationshipTypeIncludesSymmetricPropertyType() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Label label = new Label("en", "Related To");
    String uri = "urn:relType:symmetric";

    client.createSymmetricRelationshipType(label, uri);

    assertEquals(1, client.makeRequestCallCount);
    JsonObject payload = JSON.parse(client.lastPayload);
    JsonArray typeArray = payload.get("@type").getAsArray();
    List<String> types = new ArrayList<>();
    typeArray.forEach(value -> types.add(value.getAsString().value()));
    assertTrue("Should be typed as owl:ObjectProperty", types.contains("owl:ObjectProperty"));
    assertTrue("Should be typed as owl:SymmetricProperty", types.contains("owl:SymmetricProperty"));
    assertEquals(uri, payload.get("@id").getAsString().value());
    assertNull("A symmetric relationship type must not have a separate inverse", payload.get("owl:inverseOf"));
  }

  @Test
  public void createRelationshipTypeDoesNotIncludeSymmetricPropertyType() throws OEClientException {
    CapturingReadWriteClient client = newClient();
    Label forwardLabel = new Label("en", "Forward");
    Label inverseLabel = new Label("en", "Inverse");

    client.createRelationshipType(forwardLabel, "urn:relType:forward", inverseLabel, "urn:relType:inverse");

    assertEquals(1, client.makeRequestCallCount);
    assertFalse("Non-symmetric relationship types must not be typed as owl:SymmetricProperty",
        client.lastPayload.contains("owl:SymmetricProperty"));
  }

  private static CapturingReadWriteClient newClient() {
    CapturingReadWriteClient client = new CapturingReadWriteClient();
    client.setBaseURL("http://localhost");
    client.setModelUri("model:test");
    return client;
  }

  private static class CapturingReadWriteClient extends OEClientReadWrite {
    private String lastPayload;
    private String lastUrl;
    private Map<String, String> lastQueryParameters;
    private RequestType lastRequestType;
    private int makeRequestCallCount;
    private String nextMakeRequestResponse;
    private Collection<Task> tasksResponse = Collections.emptyList();

    @Override
    protected String makeRequest(String url, String payload, RequestType requestType) {
      this.lastUrl = url;
      this.lastPayload = payload;
      this.lastQueryParameters = null;
      this.lastRequestType = requestType;
      this.makeRequestCallCount++;
      return nextMakeRequestResponse;
    }

    @Override
    protected String makeRequest(String url, Map<String, String> queryParameters, String payload, RequestType requestType) {
      this.lastUrl = url;
      this.lastPayload = payload;
      this.lastQueryParameters = queryParameters;
      this.lastRequestType = requestType;
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




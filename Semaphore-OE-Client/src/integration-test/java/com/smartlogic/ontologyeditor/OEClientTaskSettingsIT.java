// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for updating task settings: label, comment, tags and permission roles.
 */
public class OEClientTaskSettingsIT extends AbstractModelScopedIT {

  private Task testTask;

  @Before
  public void createTestTask() throws OEClientException {
    if (oeClient == null) {
      return;
    }
    String taskLabel = "IT task " + UUID.randomUUID();
    testTask = oeClient.createTaskAndReturn(new Task(new Label("", taskLabel)));
  }

  @Test
  public void updateTaskLabelReplacesDisplayName() throws OEClientException {
    String newLabel = "IT task renamed " + UUID.randomUUID();
    oeClient.updateTaskLabel(testTask, testTask.getLabel().getValue(), newLabel);
    assertTrue(getTaskPropertyValues("rdfs:label").contains(newLabel));
  }

  @Test
  public void updateTaskCommentAddsAndReplacesComment() throws OEClientException {
    oeClient.updateTaskComment(testTask, null, "Initial description");
    assertTrue(getTaskPropertyValues("rdfs:comment").contains("Initial description"));

    oeClient.updateTaskComment(testTask, "Initial description", "Updated description");
    List<String> comments = getTaskPropertyValues("rdfs:comment");
    assertTrue(comments.contains("Updated description"));
    assertFalse(comments.contains("Initial description"));
  }

  @Test
  public void addAndRemoveTaskTag() throws OEClientException {
    String tag = "IT tag " + UUID.randomUUID();

    oeClient.addTaskTag(testTask, tag);
    assertTrue(getTaskPropertyValues("sem:tag").contains(tag));

    oeClient.removeTaskTag(testTask, tag);
    assertFalse(getTaskPropertyValues("sem:tag").contains(tag));
  }

  @Test
  public void assignAndUnassignTaskRole() throws OEClientException {
    String principalUri = "role:SemaphoreUsers";

    oeClient.assignTaskRole(testTask, "viewer", principalUri);
    assertTrue(getTaskPropertyValues("sempermissions:viewer").contains(principalUri));

    oeClient.unassignTaskRole(testTask, "viewer", principalUri);
    assertFalse(getTaskPropertyValues("sempermissions:viewer").contains(principalUri));
  }

  /**
   * Fetch the current values of a single property of the test task directly from the API.
   */
  private List<String> getTaskPropertyValues(String propertyUri) throws OEClientException {
    Map<String, String> queryParameters = new HashMap<>();
    queryParameters.put(OEClientReadOnly.PARAM_PROPERTIES, propertyUri);

    String url = oeClient.getApiURL() + "sys/" + testTask.getGraphUri();
    String response = oeClient.getResponse(url, queryParameters);

    JsonObject jsonResponse = JSON.parse(response);
    JsonArray graph = jsonResponse.get(OEClientReadOnly.JSON_LD_GRAPH).getAsArray();

    List<String> values = new ArrayList<>();
    if (graph.size() == 0) {
      return values;
    }

    JsonObject taskObject = graph.get(0).getAsObject();
    JsonValue propertyValue = taskObject.get(propertyUri);
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

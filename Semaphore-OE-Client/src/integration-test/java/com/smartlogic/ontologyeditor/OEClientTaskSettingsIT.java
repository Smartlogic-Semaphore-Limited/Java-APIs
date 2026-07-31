// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
  public void getTaskReturnsCurrentDisplayName() throws OEClientException {
    Task refetched = oeClient.getTask(testTask.getGraphUri());

    assertEquals(testTask.getGraphUri(), refetched.getGraphUri());
    assertEquals(testTask.getLabel().getValue(), refetched.getLabel().getValue());
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
    return getPropertyValues(testTask.getGraphUri(), propertyUri);
  }
}

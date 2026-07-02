// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for task management operations.
 * Tests task creation and commitment.
 */
public class OEClientTaskIT extends AbstractModelScopedIT {

  @Test
  public void createTaskWithLabel() throws OEClientException {
    String taskLabel = "TestTask_" + UUID.randomUUID().toString().substring(0, 8);
    Task task = new Task(new Label("en", taskLabel));

    Task newTask = oeClient.createTaskAndReturn(task);
    assertNotNull(newTask);
    assertNotNull(newTask.getGraphUri());

    boolean taskExistsOnServer = oeClient.getAllTasks().stream()
        .anyMatch(t -> newTask.getId().equals(t.getId()));
    assertTrue("Created task should be retrievable from server", taskExistsOnServer);
  }

  @Test
  public void commitTaskWithCustomLabelAndComment() throws OEClientException {
    String taskLabel = "TestTask_" + UUID.randomUUID().toString().substring(0, 8);
    Task task = new Task(new Label("en", taskLabel));
    Task newTask = oeClient.createTaskAndReturn(task);
    assertNotNull(newTask);
    assertNotNull(newTask.getGraphUri());
    String currentModelUri = oeClient.getModelUri();
    try {
      oeClient.setModelUri(newTask.getGraphUri());
      oeClient.createConceptScheme(new ConceptScheme(null, "example:schemeForTaskCommit", List.of(new Label("en", "Scheme for Task Commit"))));
    } finally {
      oeClient.setModelUri(currentModelUri);
    }
    Label commitLabel = new Label("en", "Custom commit message");
    String comment = "This is a test commit with a comment";
    assertEquals(0, oeClient.getAllConceptSchemes().size());
    oeClient.commitTask(newTask, commitLabel, comment);
    assertEquals(1, oeClient.getAllConceptSchemes().size());
  }
}



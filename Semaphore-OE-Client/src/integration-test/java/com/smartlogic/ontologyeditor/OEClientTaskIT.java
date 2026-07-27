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

  @Test
  public void deleteTaskRemovesTaskFromServer() throws OEClientException {
    String taskLabel = "TestTask_" + UUID.randomUUID().toString().substring(0, 8);
    Task task = new Task(new Label("en", taskLabel));
    Task newTask = oeClient.createTaskAndReturn(task);
    assertNotNull(newTask);
    assertNotNull(newTask.getGraphUri());

    oeClient.deleteTask(newTask);

    boolean taskExistsOnServer = oeClient.getAllTasks().stream()
        .anyMatch(t -> newTask.getId().equals(t.getId()));
    assertTrue("Deleted task should no longer be retrievable from server", !taskExistsOnServer);
  }

  @Test
  public void commitTaskUpToDateOnlyCommitsChangesMadeBeforeCutoff() throws OEClientException, InterruptedException {
    String taskLabel = "TestTask_" + UUID.randomUUID().toString().substring(0, 8);
    Task task = new Task(new Label("en", taskLabel));
    Task newTask = oeClient.createTaskAndReturn(task);
    assertNotNull(newTask);
    assertNotNull(newTask.getGraphUri());

    String currentModelUri = oeClient.getModelUri();
    try {
      oeClient.setModelUri(newTask.getGraphUri());
      oeClient.createConceptScheme(new ConceptScheme(null, "example:schemeBeforeCutoff", List.of(new Label("en", "Scheme Before Cutoff"))));
      // Ensure the cut-off timestamp falls strictly between the two changes.
      Thread.sleep(1500);
      java.util.Date cutoff = new java.util.Date();
      Thread.sleep(1500);
      oeClient.createConceptScheme(new ConceptScheme(null, "example:schemeAfterCutoff", List.of(new Label("en", "Scheme After Cutoff"))));

      oeClient.setModelUri(currentModelUri);
      assertEquals(0, oeClient.getAllConceptSchemes().size());

      oeClient.commitTask(newTask, new Label("en", "Partial commit"), "Commit up to cutoff", cutoff);

      assertEquals("Only the change made before the cutoff should be committed to master",
          1, oeClient.getAllConceptSchemes().size());
      assertTrue("Master should contain the change made before the cutoff",
          oeClient.getAllConceptSchemes().stream()
              .anyMatch(scheme -> "example:schemeBeforeCutoff".equals(scheme.getUri())));

      oeClient.setModelUri(newTask.getGraphUri());
      // The task's view merges master's committed changes with its own remaining uncommitted
      // changes, so we assert on presence of the specific scheme rather than a total count.
      assertTrue("The change made after the cutoff should remain uncommitted on the task",
          oeClient.getAllConceptSchemes().stream()
              .anyMatch(scheme -> "example:schemeAfterCutoff".equals(scheme.getUri())));
    } finally {
      oeClient.setModelUri(currentModelUri);
    }
  }
}



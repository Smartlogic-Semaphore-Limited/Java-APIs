// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.Task;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration tests for {@link OEClientReadWrite#importTurtle(String, String)} and its
 * {@code checkConstraints} overload, exercised against a real Workbench instance (see
 * {@link AbstractOEIntegrationTest} for the required environment variables).
 *
 * <p>Each test runs against a freshly created, isolated model (see {@link AbstractModelScopedIT}).
 */
public class OEClientTurtleImportIT extends AbstractModelScopedIT {

  @Test
  public void importTurtleAddsConceptSchemeAndConceptsToModel() throws OEClientException {
    String namespace = testModel.getDefaultNamespace();
    String schemeUri = namespace + "scheme1";
    String conceptUri = namespace + "concept1";
    String turtle = "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n" +
        "@prefix ex: <" + namespace + "> .\n" +
        "ex:scheme1 a skos:ConceptScheme ;\n" +
        "    skos:prefLabel \"Imported Scheme\"@en .\n" +
        "ex:concept1 a skos:Concept ;\n" +
        "    skos:prefLabel \"Imported Concept\"@en ;\n" +
        "    skos:inScheme ex:scheme1 ;\n" +
        "    skos:topConceptOf ex:scheme1 .\n" +
        "ex:scheme1 skos:hasTopConcept ex:concept1 .\n";

    oeClient.importTurtle(testModel.getUri(), turtle);

    boolean schemeExists = oeClient.getAllConceptSchemes().stream()
        .anyMatch(scheme -> schemeUri.equals(scheme.getUri()));
    assertTrue("Imported concept scheme should be retrievable from server", schemeExists);

    var concept = oeClient.getConcept(conceptUri);
    assertTrue("Imported concept should have the expected preferred label",
        concept.getPrefLabels().stream().anyMatch(label -> "Imported Concept".equals(label.getValue())));
  }

  @Test
  public void importTurtleWithCheckConstraintsFalseStillImports() throws OEClientException {
    String namespace = testModel.getDefaultNamespace();
    String conceptUri = namespace + "concept-no-check";
    String turtle = "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n" +
        "@prefix ex: <" + namespace + "> .\n" +
        "ex:concept-no-check a skos:Concept ;\n" +
        "    skos:prefLabel \"No Constraint Check\"@en .\n";

    oeClient.importTurtle(testModel.getUri(), turtle, false);

    var concept = oeClient.getConcept(conceptUri);
    assertTrue(concept.getPrefLabels().stream()
        .anyMatch(label -> "No Constraint Check".equals(label.getValue())));
  }

  @Test
  public void importTurtleIntoTaskGraphKeepsMasterModelUnchanged() throws OEClientException {
    String taskLabel = "TurtleImportTask_" + UUID.randomUUID().toString().substring(0, 8);
    Task task = oeClient.createTaskAndReturn(new Task(new Label("en", taskLabel)));
    String namespace = testModel.getDefaultNamespace();
    String schemeUri = namespace + "taskScheme";
    String turtle = "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n" +
        "@prefix ex: <" + namespace + "> .\n" +
        "ex:taskScheme a skos:ConceptScheme ;\n" +
        "    skos:prefLabel \"Task Scheme\"@en .\n";

    oeClient.importTurtle(task.getGraphUri(), turtle);

    assertEquals("Master model should not contain the task-only scheme", 0,
        oeClient.getAllConceptSchemes().stream()
            .filter(scheme -> schemeUri.equals(scheme.getUri())).count());

    String currentModelUri = oeClient.getModelUri();
    try {
      oeClient.setModelUri(task.getGraphUri());
      boolean schemeExistsOnTask = oeClient.getAllConceptSchemes().stream()
          .anyMatch(scheme -> schemeUri.equals(scheme.getUri()));
      assertTrue("Task graph should contain the imported scheme", schemeExistsOnTask);
    } finally {
      oeClient.setModelUri(currentModelUri);
    }
  }

  @Test
  public void importTurtleRejectsMalformedTurtleWithoutCommittingAnything() throws OEClientException {
    long countBefore = oeClient.getConceptCount();

    try {
      oeClient.importTurtle(testModel.getUri(), "this is not valid turtle @@@");
      fail("Expected OEClientException for malformed Turtle");
    } catch (OEClientException e) {
      assertTrue("Exception should surface the server's rejection details",
          e.getMessage() != null && !e.getMessage().isBlank());
    }

    assertEquals("Malformed Turtle must not commit any data", countBefore, oeClient.getConceptCount());
  }

  /**
   * A concept declared as its own broader concept is a self-referencing hierarchical cycle,
   * which violates Semaphore's built-in taxonomy integrity constraints. This exercises the
   * atomic, in-transaction constraint validation added for {@code checkConstraints}: the
   * violation must roll back the entire import so nothing is committed.
   *
   * <p>If a future Workbench version stops treating this particular case as a violation,
   * replace it with another constraint known to be enforced in the target environment; the
   * intent of this test is to prove that violations roll back the import, not to pin down one
   * specific rule.
   */
  @Test
  public void importTurtleWithCheckConstraintsTrueRollsBackOnCycleViolation() throws OEClientException {
    String namespace = testModel.getDefaultNamespace();
    String turtle = "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n" +
        "@prefix ex: <" + namespace + "> .\n" +
        "ex:cyclicConcept a skos:Concept ;\n" +
        "    skos:prefLabel \"Cyclic Concept\"@en ;\n" +
        "    skos:broader ex:cyclicConcept .\n";
    long countBefore = oeClient.getConceptCount();

    try {
      oeClient.importTurtle(testModel.getUri(), turtle, true);
      fail("Expected a constraint violation for a self-referencing broader relationship");
    } catch (OEClientException e) {
      assertTrue("Violation details should be present", e.getMessage() != null && !e.getMessage().isBlank());
    }

    assertEquals("Nothing should be committed when constraint validation fails",
        countBefore, oeClient.getConceptCount());
  }
}

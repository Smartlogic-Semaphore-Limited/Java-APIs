// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Integration tests for updating model settings: comment, default namespace, color and tags.
 */
public class OEClientModelSettingsIT extends AbstractModelScopedIT {

  @Test
  public void updateModelCommentAddsAndReplacesComment() throws OEClientException {
    oeClient.updateModelComment(testModel, null, "Initial description");
    assertTrue(getModelPropertyValues("rdfs:comment").contains("Initial description"));

    oeClient.updateModelComment(testModel, "Initial description", "Updated description");
    List<String> comments = getModelPropertyValues("rdfs:comment");
    assertTrue(comments.contains("Updated description"));
    assertFalse(comments.contains("Initial description"));
  }

  @Test
  public void getModelReturnsCurrentCommentAndDefaultNamespace() throws OEClientException {
    oeClient.updateModelComment(testModel, null, "Description for getModel");
    String newNamespace = "http://example.test/" + UUID.randomUUID() + "#";
    oeClient.updateModelDefaultNamespace(testModel, testModel.getDefaultNamespace(),
        newNamespace);

    com.smartlogic.ontologyeditor.beans.Model refetched = oeClient.getModel(testModel.getUri());

    assertEquals("Description for getModel", refetched.getComment());
    assertEquals(newNamespace, refetched.getDefaultNamespace());
  }

  @Test
  public void updateModelDefaultNamespaceReplacesNamespace() throws OEClientException {
    String oldNamespace = testModel.getDefaultNamespace();
    String newNamespace = "http://example.test/" + UUID.randomUUID() + "#";

    oeClient.updateModelDefaultNamespace(testModel, oldNamespace, newNamespace);

    List<String> namespaces = getModelPropertyValues("swa:defaultNamespace");
    assertTrue(namespaces.contains(newNamespace));
    assertFalse(namespaces.contains(oldNamespace));
  }

  @Test
  public void updateModelColorAddsAndReplacesColor() throws OEClientException {
    oeClient.updateModelColor(testModel, null, "00c851");
    assertTrue(getModelPropertyValues("sem:color").contains("00c851"));

    oeClient.updateModelColor(testModel, "00c851", "7fa38e");
    List<String> colors = getModelPropertyValues("sem:color");
    assertTrue(colors.contains("7fa38e"));
    assertFalse(colors.contains("00c851"));
  }

  @Test
  public void addAndRemoveModelTag() throws OEClientException {
    String tag = "IT tag " + UUID.randomUUID();

    oeClient.addModelTag(testModel, tag);
    assertTrue(getModelPropertyValues("sem:tag").contains(tag));

    oeClient.removeModelTag(testModel, tag);
    assertFalse(getModelPropertyValues("sem:tag").contains(tag));
  }

  @Test
  public void assignAndUnassignModelRole() throws OEClientException {
    String principalUri = "role:SemaphoreUsers";

    oeClient.assignModelRole(testModel, "viewer", principalUri);
    assertTrue(getModelPropertyValues("sempermissions:viewer").contains(principalUri));

    oeClient.unassignModelRole(testModel, "viewer", principalUri);
    assertFalse(getModelPropertyValues("sempermissions:viewer").contains(principalUri));
  }

  /**
   * Fetch the current values of a single property of the test model directly from the API,
   * bypassing the {@link com.smartlogic.ontologyeditor.beans.Model} bean (which only exposes
   * a fixed subset of properties).
   */
  private List<String> getModelPropertyValues(String propertyUri) throws OEClientException {
    return getPropertyValues(testModel.getUri(), propertyUri);
  }
}

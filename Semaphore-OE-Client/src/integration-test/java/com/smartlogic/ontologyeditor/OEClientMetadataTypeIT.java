// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Label;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for metadata type management.
 * Tests creation and deletion of various metadata types.
 */
public class OEClientMetadataTypeIT extends AbstractModelScopedIT {

  @Test
  public void createBooleanMetadataType() throws OEClientException {
    String metadataTypeUri = "http://example.test/booleanMetadata_" + UUID.randomUUID();
    Label label = new Label("en", "Boolean Metadata Type");

    oeClient.createMetadataTypeBoolean(label, metadataTypeUri);

    oeClient.getMetadataTypes().stream().filter(mt -> mt.getUri().equals(metadataTypeUri)).findFirst()
        .orElseThrow(() -> new AssertionError("Boolean metadata type not found after creation"));
  }

  @Test
  public void createStringMetadataType() throws OEClientException {
    String metadataTypeUri = "http://example.test/stringMetadata_" + UUID.randomUUID();
    Label label = new Label("en", "String Metadata Type");

    oeClient.createMetadataTypeString(label, metadataTypeUri);

    oeClient.getMetadataTypes().stream().filter(mt -> mt.getUri().equals(metadataTypeUri)).findFirst()
        .orElseThrow(() -> new AssertionError("String metadata type not found after creation"));
  }

  @Test
  public void deleteMetadataType() throws OEClientException {
    String metadataTypeUri = "http://example.test/stringMetadata_" + UUID.randomUUID();
    Label label = new Label("en", "String Metadata Type");

    String metadataTypeString = oeClient.createMetadataTypeString(label, metadataTypeUri);
    oeClient.deleteMetadataType(metadataTypeString);

    oeClient.getMetadataTypes().stream().filter(mt -> mt.getUri().equals(metadataTypeUri)).findAny()
            .ifPresent(mt -> { throw new AssertionError("String metadata type still found after deletion"); });
  }

  @Test
  public void createIntegerMetadataType() throws OEClientException {
    String metadataTypeUri = "http://example.test/intMetadata_" + UUID.randomUUID();
    Label label = new Label("en", "Integer Metadata Type");

    oeClient.createMetadataTypeInteger(label, metadataTypeUri);

    oeClient.getMetadataTypes().stream().filter(mt -> mt.getUri().equals(metadataTypeUri)).findFirst()
        .orElseThrow(() -> new AssertionError("Integer metadata type not found after creation"));
  }

  @Test
  public void createDateMetadataType() throws OEClientException {
    String metadataTypeUri = "http://example.test/dateMetadata_" + UUID.randomUUID();
    Label label = new Label("en", "Date Metadata Type");

    oeClient.createMetadataTypeDate(label, metadataTypeUri);

    oeClient.getMetadataTypes().stream().filter(mt -> mt.getUri().equals(metadataTypeUri)).findFirst()
        .orElseThrow(() -> new AssertionError("Date metadata type not found after creation"));
  }

  @Test
  public void createDecimalMetadataType() throws OEClientException {
    String metadataTypeUri = "http://example.test/decimalMetadata_" + UUID.randomUUID();
    Label label = new Label("en", "Decimal Metadata Type");

    oeClient.createMetadataTypeDecimal(label, metadataTypeUri);

    oeClient.getMetadataTypes().stream().filter(mt -> mt.getUri().equals(metadataTypeUri)).findFirst()
        .orElseThrow(() -> new AssertionError("Decimal metadata type not found after creation"));
  }

  @Test
  public void createUriMetadataType() throws OEClientException {
    String metadataTypeUri = "http://example.test/uriMetadata_" + UUID.randomUUID();
    Label label = new Label("en", "URI Metadata Type");

    oeClient.createMetadataTypeAnyURI(label, metadataTypeUri);

    oeClient.getMetadataTypes().stream().filter(mt -> mt.getUri().equals(metadataTypeUri)).findFirst()
        .orElseThrow(() -> new AssertionError("URI metadata type not found after creation"));
  }
}


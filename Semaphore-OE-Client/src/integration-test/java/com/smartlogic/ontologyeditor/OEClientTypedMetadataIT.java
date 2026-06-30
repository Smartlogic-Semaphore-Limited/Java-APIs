// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.ontologyeditor.beans.Concept;
import com.smartlogic.ontologyeditor.beans.ConceptScheme;
import com.smartlogic.ontologyeditor.beans.Label;
import com.smartlogic.ontologyeditor.beans.MetadataValue;
import org.junit.Test;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for typed metadata operations (update/delete with typed values).
 * Each test runs against a freshly created model (see {@link AbstractModelScopedIT}).
 */
public class OEClientTypedMetadataIT extends AbstractModelScopedIT {

  @Test
  public void updateIntegerMetadata() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeInteger(new Label("en", "rank"), "http://example.test/rank");

    oeClient.createMetadata(concept, metadataTypeUri, 5);
    oeClient.updateMetadata(concept, metadataTypeUri, 5, 10);

    assertMetadataValue(concept, metadataTypeUri, 10);
  }



  @Test
  public void updateDecimalMetadata() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeDecimal(new Label("en", "score"), "http://example.test/score");

    oeClient.createMetadata(concept, metadataTypeUri, 3.14159d);
    oeClient.updateMetadata(concept, metadataTypeUri, 3.14159d, 2.71828d);

    assertMetadataValue(concept, metadataTypeUri, 2.71828d);
  }

  @Test
  public void updateDateMetadata() throws OEClientException {
    Date oldDate = new Date(1609459200000L); // 2021-01-01
    Date newDate = new Date(1640995200000L); // 2022-01-01
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeDate(new Label("en", "date"), "http://example.test/date");

    oeClient.createMetadata(concept, metadataTypeUri, oldDate);

    oeClient.updateMetadata(concept, metadataTypeUri, oldDate, newDate);

    assertMetadataValue(concept, metadataTypeUri, "2022-01-01");
  }

  @Test
  public void updateUriMetadata() throws OEClientException {
    URI oldUri = URI.create("https://example.test/old");
    URI newUri = URI.create("https://example.test/new");
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeAnyURI(new Label("en", "link"), "http://example.test/link");

    oeClient.createMetadata(concept, metadataTypeUri, oldUri);

    oeClient.updateMetadata(concept, metadataTypeUri, oldUri, newUri);

    assertMetadataValue(concept, metadataTypeUri, newUri);
  }

  @Test
  public void deleteIntegerMetadata() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeInteger(new Label("en", "rank"), "http://example.test/rank");

    oeClient.createMetadata(concept, metadataTypeUri, 42);

    oeClient.deleteMetadata(concept, metadataTypeUri, 42);

    assertMetadataValue(concept, metadataTypeUri, null);
  }

  @Test
  public void deleteDecimalMetadata() throws OEClientException {
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeDecimal(new Label("en", "score"), "http://example.test/score");

    oeClient.createMetadata(concept, metadataTypeUri, 99.99d);

    oeClient.deleteMetadata(concept, metadataTypeUri, 99.99d);

    assertMetadataValue(concept, metadataTypeUri, null);
  }

  @Test
  public void deleteDateMetadata() throws OEClientException {
    Date testDate = new Date(1577836800000L); // 2020-01-01
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeDate(new Label("en", "date"), "http://example.test/date");

    oeClient.createMetadata(concept, metadataTypeUri, testDate);

    oeClient.deleteMetadata(concept, metadataTypeUri, testDate);

    assertMetadataValue(concept, metadataTypeUri, null);
  }

  @Test
  public void deleteUriMetadata() throws OEClientException {
    URI testUri = URI.create("https://example.test/resource");
    ConceptScheme scheme = createTestScheme();
    Concept concept = createTestConcept(scheme);
    String metadataTypeUri = oeClient.createMetadataTypeAnyURI(new Label("en", "link"), "http://example.test/link");
    oeClient.createMetadata(concept, metadataTypeUri, testUri);

    oeClient.deleteMetadata(concept, metadataTypeUri, testUri);

    assertMetadataValue(concept, metadataTypeUri, null);
  }

  private void assertMetadataValue(Concept concept, String metadataTypeUri, Object expectedValue) throws OEClientException {
    Concept result = oeClient.getConcept(concept.getUri());
    oeClient.populateMetadata(metadataTypeUri, result);
    Collection<MetadataValue> metadata = result.getMetadata(metadataTypeUri);
    if(expectedValue == null) {
      assertEquals(0, metadata.size());
      return;
    }
    assertEquals(1, metadata.size());
    assertEquals(expectedValue.toString(), metadata.iterator().next().getValue());
  }

  private Concept createTestConcept(ConceptScheme scheme) throws OEClientException {
    Concept concept = new Concept(oeClient, "http://example.test/concept/" + UUID.randomUUID(),
        List.of(new Label("en", "Test")));
    oeClient.createConcept(scheme.getUri(), concept);
    return concept;
  }

  private ConceptScheme createTestScheme() throws OEClientException {
    String uniqueName = "TypedMetaScheme_" + UUID.randomUUID().toString().substring(0, 8);
    ConceptScheme scheme = new ConceptScheme(oeClient, "http://example.test/scheme/" + uniqueName,
        List.of(new Label("en", uniqueName)));
    oeClient.createConceptScheme(scheme);
    return scheme;
  }
}

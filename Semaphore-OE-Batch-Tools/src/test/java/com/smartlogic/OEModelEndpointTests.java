package com.smartlogic;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateAction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.time.Duration;

import static org.junit.Assert.assertEquals;

public class OEModelEndpointTests {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testUrlEndpoints() throws Exception {
    OEModelEndpoint ep = new OEModelEndpoint();
    ep.setModelIRI(URI.create("model:ModelID").toString());
    ep.setAccessToken("ACCESSTOKEN");
    ep.setBaseUrl("http://localhost:5080");

    assertEquals("http://localhost:5080/kmm/api/", ep.buildApiUrl().toString());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql", ep.buildSPARQLUrl(null));

    ep.setBaseUrl("http://localhost:5080/");
    ep.setModelIRI("model:TestAnotherModelID");
    assertEquals("http://localhost:5080/kmm/api/", ep.buildApiUrl().toString());
    assertEquals(
            "http://localhost:5080/kmm/api/model:TestAnotherModelID/sparql", ep.buildSPARQLUrl(null));
    assertEquals(
            "http://localhost:5080/kmm/api/model:TestAnotherModelID/sparql?async=true&runEditRules=true&checkConstraints=true",
            ep.buildSPARQLUrl());
    SparqlUpdateOptions options = new SparqlUpdateOptions();
    options.setAcceptWarnings(true);
    assertEquals(
            "http://localhost:5080/kmm/api/model:TestAnotherModelID/sparql?async=true&warningsAccepted=true&runEditRules=true&checkConstraints=true",
            ep.buildSPARQLUrl(options));

    ep.setBaseUrl("http://myserver.mydomain.com:9999/");
    ep.setModelIRI("model:TestID");
    assertEquals("http://myserver.mydomain.com:9999/kmm/api/", ep.buildApiUrl().toString());
    assertEquals(
            "http://myserver.mydomain.com:9999/kmm/api/model:TestID/sparql", ep.buildSPARQLUrl(null));

    ep.setConnectTimeout(Duration.ofMinutes(11));
    assertEquals(ep.getConnectTimeout(), Duration.ofMinutes(11));
    ep.setConnectTimeout(Duration.ofMinutes(7));
    assertEquals(ep.getConnectTimeout(), Duration.ofMinutes(7));

    ep.setRequestTimeout(Duration.ofMinutes(33));
    assertEquals(ep.getRequestTimeout(), Duration.ofMinutes(33));
    ep.setRequestTimeout(Duration.ofMinutes(7));
    assertEquals(ep.getRequestTimeout(), Duration.ofMinutes(7));

  }

  @Test
  public void testBadSparql() {
    String sparql = "SELECT { AA";
    try {
      QueryFactory.create(sparql);
      Assert.fail("Failed to detect bad SPARQL query");
    } catch (Exception e) {}

    try {
      UpdateAction.parseExecute(sparql, ModelFactory.createDefaultModel());
      Assert.fail("Failed to detect bad SPARQL update");
    } catch (Exception e) {}
  }

  @Test
  public void testCloudSparql() {
    OEModelEndpoint ep = new OEModelEndpoint();
    ep.setModelIRI(URI.create("model:ModelID").toString());
    ep.setCloudTokenFetchUrl("https://cloud.smartlogic.com/token");
    ep.setCloudAPIKey("my-api-key");
    ep.setBaseUrl("http://localhost:5080");

    assertEquals("my-api-key", ep.getCloudAPIKey());
    assertEquals("https://cloud.smartlogic.com/token", ep.getCloudTokenFetchUrl());
    assertEquals("http://localhost:5080/kmm/api/", ep.buildApiUrl().toString());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?async=true&runEditRules=true&checkConstraints=true", ep.buildSPARQLUrl());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql", ep.buildSPARQLUrl(null));
    SparqlUpdateOptions options = new SparqlUpdateOptions();
    options.acceptWarnings = false;
    options.runCheckConstraints = true;
    options.runEditRules = false;
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?async=true&runEditRules=false&checkConstraints=true",
            ep.buildSPARQLUrl(options));
    options.acceptWarnings = true;
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?async=true&warningsAccepted=true&runEditRules=false&checkConstraints=true",
            ep.buildSPARQLUrl(options));

  }
}

package com.smartlogic;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.update.UpdateAction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
    ep.setModelIRI(IRIResolver.parseIRI("model:ModelID").toString());
    ep.setAccessToken("ACCESSTOKEN");
    ep.setBaseUrl("http://localhost:5080");

    assertEquals("http://localhost:5080/t/ACCESSTOKEN/kmm/api", ep.buildApiUrl().toString());
    assertEquals("http://localhost:5080/t/ACCESSTOKEN/kmm/api/model:ModelID/sparql", ep.buildSPARQLUrl(null));

    ep.setBaseUrl("http://localhost:5080/");
    ep.setModelIRI("model:TestAnotherModelID");
    assertEquals("http://localhost:5080/t/ACCESSTOKEN/kmm/api", ep.buildApiUrl().toString());
    assertEquals(
            "http://localhost:5080/t/ACCESSTOKEN/kmm/api/model:TestAnotherModelID/sparql", ep.buildSPARQLUrl(null));
    assertEquals(
            "http://localhost:5080/t/ACCESSTOKEN/kmm/api/model:TestAnotherModelID/sparql?runEditRules=true&checkConstraints=true",
            ep.buildSPARQLUrl());
    SparqlUpdateOptions options = new SparqlUpdateOptions();
    options.setAcceptWarnings(true);
    assertEquals(
            "http://localhost:5080/t/ACCESSTOKEN/kmm/api/model:TestAnotherModelID/sparql?warningsAccepted=true&runEditRules=true&checkConstraints=true",
            ep.buildSPARQLUrl(options));

    ep.setBaseUrl("http://myserver.mydomain.com:9999/");
    ep.setModelIRI("model:TestID");
    assertEquals("http://myserver.mydomain.com:9999/t/ACCESSTOKEN/kmm/api", ep.buildApiUrl().toString());
    assertEquals(
            "http://myserver.mydomain.com:9999/t/ACCESSTOKEN/kmm/api/model:TestID/sparql", ep.buildSPARQLUrl(null));

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
    ep.setModelIRI(IRIResolver.parseIRI("model:ModelID").toString());
    ep.setCloudTokenFetchUrl("https://cloud.smartlogic.com/token");
    ep.setCloudAPIKey("my-api-key");
    ep.setBaseUrl("http://localhost:5080");

    assertEquals("my-api-key", ep.getCloudAPIKey());
    assertEquals("https://cloud.smartlogic.com/token", ep.getCloudTokenFetchUrl());
    assertEquals("http://localhost:5080/kmm/api", ep.buildApiUrl().toString());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?runEditRules=true&checkConstraints=true", ep.buildSPARQLUrl());
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql", ep.buildSPARQLUrl(null));
    SparqlUpdateOptions options = new SparqlUpdateOptions();
    options.acceptWarnings = false;
    options.runCheckConstraints = true;
    options.runEditRules = false;
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?runEditRules=false&checkConstraints=true",
            ep.buildSPARQLUrl(options));
    options.acceptWarnings = true;
    assertEquals("http://localhost:5080/kmm/api/model:ModelID/sparql?warningsAccepted=true&runEditRules=false&checkConstraints=true",
            ep.buildSPARQLUrl(options));

  }
}

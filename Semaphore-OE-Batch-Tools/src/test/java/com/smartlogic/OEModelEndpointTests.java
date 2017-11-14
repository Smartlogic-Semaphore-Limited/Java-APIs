package com.smartlogic;

import static org.junit.Assert.assertEquals;

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
    ep.modelIRI = IRIResolver.parseIRI("urn:x-evn-master:ModelID");
    ep.accessToken = "ACCESSTOKEN";
    ep.baseURL = "http://localhost:8080/swoe/";

    assertEquals(ep.buildApiUrl().toString(), "http://localhost:8080/swoe/api/t/ACCESSTOKEN");
    assertEquals(ep.buildSPARQLUrl().toString(),
        "http://localhost:8080/swoe/api/t/ACCESSTOKEN/urn:x-evn-master:ModelID/sparql");
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
}

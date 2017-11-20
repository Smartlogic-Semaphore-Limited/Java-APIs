package com.smartlogic;

import com.smartlogic.rdfdiff.RDFDifference;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.system.IRIResolver;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OEBatchClientTests {

  static String resIri1 = "urn:test:res1";
  static String resIri2 = "urn:test:res2";

  Model model;
  OEBatchClient client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    model = ModelFactory.createDefaultModel();
    model.read("Playpen2.ttl", "TTL");
    OEModelEndpoint endpoint = new OEModelEndpoint();
    endpoint.modelIri = IRIResolver.parseIRI("urn:test:model-x").toString();
    client = new OEBatchClient(endpoint);

  }

  @After
  public void tearDown() throws Exception {
    model.close();
  }

  @Test
  public void testLocalDiff() throws Exception {
    Model firstModel = ModelFactory.createDefaultModel();
    firstModel.read("Playpen2.ttl", "TTL");

    Model secondModel = ModelFactory.createDefaultModel();
    secondModel.add(firstModel);
    Resource res1 = secondModel.createResource(resIri1);
    res1.addProperty(RDF.type, SKOS.Concept);

    client.setCurrentModel(firstModel);
    client.setPendingModel(secondModel);
    assertEquals(firstModel, client.getCurrentModel());
    assertEquals(secondModel, client.getPendingModel());

    RDFDifference diff = client.getBatchDiff();

    assertTrue(diff.getInLeftOnly().size() == 0);
    assertTrue(diff.getInRightOnly().size() == 1);

    assertFalse(diff.getInLeftOnly().containsResource(res1));
    assertFalse(diff.getInLeftOnly().contains(res1, RDF.type, SKOS.Concept));

    assertTrue(diff.getInRightOnly().containsResource(res1));
    assertTrue(diff.getInRightOnly().contains(res1, RDF.type, SKOS.Concept));
  }

}

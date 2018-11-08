package com.smartlogic;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;

import java.util.Properties;
import java.util.UUID;

public class ExampleClient {

  public static void main(String[] args) {
    try {
      test1();
      test2();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.printf("Tests completed");
  }

  public static void test1() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("oeurl"));
      endpoint.setModelIRI("model:myExample");
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        client.getSparqlUpdateOptions().runCheckConstraints = false;
        client.getSparqlUpdateOptions().runEditRules = false;
        client.getSparqlUpdateOptions().acceptWarnings = true;
        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
        Resource r = m.createResource("http://myexample.com/Scheme4");
        r.addProperty(RDF.type, SKOS.ConceptScheme);
        r.addProperty(RDFS.label, "Test Scheme 4 (Test)");
        r.addProperty(semGuid, UUID.randomUUID().toString());

        Resource c = m.createResource("http://myexample.com/MyTestConcept1");
        c.addProperty(RDF.type, SKOS.Concept);
        c.addProperty(semGuid, UUID.randomUUID().toString());

        Resource cl = m.createResource("http://myexample.com/MyTestConcept1_prefLabel_en");
        cl.addProperty(SKOSXL.literalForm, "My Test Concept 1", "en");
        cl.addProperty(RDF.type, SKOSXL.Label);

        c.addProperty(SKOSXL.prefLabel, cl);

        r.addProperty(SKOS.hasTopConcept, c);

        client.commit();


      } finally {

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void test2() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("oeurl"));
      endpoint.setModelIRI("model:myExample");
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
        Resource r = m.createResource("http://myexample.com/Scheme4");
        r.addProperty(RDF.type, SKOS.ConceptScheme);
        r.addProperty(RDFS.label, "Test Scheme 4 (Test)");
        r.addProperty(semGuid, UUID.randomUUID().toString());

        Resource c = m.createResource("http://myexample.com/MyTestConcept2");
        c.addProperty(RDF.type, SKOS.Concept);
        c.addProperty(semGuid, UUID.randomUUID().toString());

        Resource cl = m.createResource("http://myexample.com/MyTestConcept2_prefLabel_en");
        cl.addProperty(SKOSXL.literalForm, "My Test Concept 1", "en");
        cl.addProperty(RDF.type, SKOSXL.Label);

        c.addProperty(SKOSXL.prefLabel, cl);

        r.addProperty(SKOS.hasTopConcept, c);

        // this test should error out unless check constraints are disabled...
        if (false == client.commit()) {
          System.out.println("Failed as expected due to duplicate English concept label created during test1");
        } else {
          throw new RuntimeException("Should have failed on constraint violation!");
        }
        // check result manually

      } catch (Exception e) {
        System.out.println("Successfully caught exception from commit! Text:" + e.getMessage());
      } finally {

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

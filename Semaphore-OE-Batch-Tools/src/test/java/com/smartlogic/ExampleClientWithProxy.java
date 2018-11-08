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

public class ExampleClientWithProxy {
  public static void main(String[] args) {
    try {

      OEModelEndpoint endpoint = new OEModelEndpoint();
      Properties config = TestConfig.getConfig();

      endpoint.setBaseUrl(config.getProperty("oeurl"));
      endpoint.setModelIRI("model:Playpen");
      endpoint.setProxyHost(config.getProperty("proxyhost"));
      endpoint.setProxyPort(Integer.valueOf(config.getProperty("proxyport")));
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
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
        cl.addProperty(SKOSXL.literalForm, "My Test Concept 1");
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
}

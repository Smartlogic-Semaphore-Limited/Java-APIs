package com.smartlogic;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;

import java.util.UUID;

public class ExampleClientWithProxy {
  public static void main(String[] args) {
    try {

      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl("http://localhost:8080/Semaphore-4.1.20-SemaphoreWorkbenchOntologyEditor");
      endpoint.setModelIRI("model:Playpen");
      endpoint.setProxyHost("localhost");
      endpoint.setProxyPort(8888);
      endpoint.setAccessToken("WyJBZG1pbmlzdHJhdG9yIiwxNTI3NjgyNTA5LCJNQ0FDRGo1T2MzR1NoK3NIUHlWYkQ4U2ZBZzVVNXpQMnRFL0YrUVZCeWEzajB3PT0iXQ==");

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

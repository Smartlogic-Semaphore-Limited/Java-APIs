package com.smartlogic;

import com.smartlogic.oebatch.beans.JobResult;
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
      createSchemeConceptTest();
      tryToCreateDuplicateLabelConceptTest();
      loopToItselfShouldFailTest();
      secondLoopShouldFailTest();
      bigBeautifulYuugeBulkAddTest();
      bigWithBatchTest();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.printf("Tests completed");
  }

  public static void createSchemeConceptTest() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("studiourl"));
      endpoint.setModelIRI(config.getProperty("modeluri"));
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      if (config.containsKey("xuser")) {
        endpoint.setHeader("X-User", config.getProperty("xuser"));
      }
      if (config.containsKey("xrole")) {
        endpoint.setHeader("X-Role", config.getProperty("xrole"));
      }

      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        /* we are running this test in micro-batches. So we turn off all edit rules and constraint checking */
        client.getSparqlUpdateOptions().setRunCheckConstraints(false);
        client.getSparqlUpdateOptions().setRunEditRules(false);
        /* batching enabled, threshold = 1 for testing */
        client.setBatchEnabled(true);
        client.setBatchThreshold(1);

        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Resource c = createSchemeAndConcept(m);
        System.out.println("definition (with whitespace): " + c.getProperty(SKOS.definition));
        if (!client.commit()) {
          System.out.println("commit failed:" + client.getCommitJobResult().errors().stream().findFirst().orElse(null));
          throw new RuntimeException("Failed to commit first step model, commit returned false");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Resource createSchemeAndConcept(Model m) {
    Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
    Resource r = m.createResource("http://myexample.com/Scheme4");
    r.addProperty(RDF.type, SKOS.ConceptScheme);
    r.addProperty(RDFS.label, "Test Scheme 4 (Test)");
    r.addProperty(semGuid, UUID.randomUUID().toString());

    Resource c = m.createResource("http://myexample.com/MyTestConcept1");
    c.addProperty(RDF.type, SKOS.Concept);
    c.addProperty(semGuid, UUID.randomUUID().toString());

    c.addProperty(SKOS.definition, "\"Here is some\tspecial\r\n stuff for us to \' look at\n\"");

    Resource cl = m.createResource("http://myexample.com/MyTestConcept1_prefLabel_en");
    cl.addProperty(SKOSXL.literalForm, "My Test Concept 1", "en");
    cl.addProperty(RDF.type, SKOSXL.Label);

    c.addProperty(SKOSXL.prefLabel, cl);

    r.addProperty(SKOS.hasTopConcept, c);

    return c;
  }

  public static void tryToCreateDuplicateLabelConceptTest() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("studiourl"));
      endpoint.setModelIRI(config.getProperty("modeluri"));
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
        Resource res = m.createResource("http://myexample.com/Scheme4");

        Resource c = m.createResource("http://myexample.com/MyTestConcept2");
        c.addProperty(RDF.type, SKOS.Concept);
        c.addProperty(semGuid, UUID.randomUUID().toString());

        Resource cl = m.createResource("http://myexample.com/MyTestConcept2_prefLabel_en");
        cl.addProperty(SKOSXL.literalForm, "My Test Concept 1", "en");
        cl.addProperty(RDF.type, SKOSXL.Label);

        c.addProperty(SKOSXL.prefLabel, cl);

        res.addProperty(SKOS.hasTopConcept, c);

        // this test should error out unless check constraints are disabled...
        boolean resultIsSuccessful = client.commit();
        if (!resultIsSuccessful) {
          System.out.println("Failed as expected due to duplicate English concept label created during test1");
          System.out.println("commit failed, errors:");
          JobResult rec = client.getCommitJobResult();
          System.out.println("Job Id          : " + rec.jobId());
          System.out.println("HTTP status code: " + rec.httpStatusCode());
          System.out.println("errors          :");
          rec.errors().forEach( err -> {
            System.out.println("          type: " + err.errorType());
            System.out.println("   error level: " + err.params().errorLevel());
            System.out.println(" constraint id: " + err.params().constraintId());
            System.out.println("       message: " + err.params().message());
            System.out.println("          root: " + err.params().root());
          });
        } else {
          throw new RuntimeException("Should have failed on constraint violation!");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void loopToItselfShouldFailTest() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("studiourl"));
      endpoint.setModelIRI(config.getProperty("modeluri"));
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Resource sr = m.createResource("http://myexample.com/Scheme4");
        Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
        Resource res = m.createResource("http://myexample.com/MyTestConcept2");
        Resource rl = m.createResource("http://myexample.com/MyTestConcept2_prefLabel_en");
        res.addProperty(RDF.type, SKOS.Concept);
        res.addProperty(SKOSXL.prefLabel, rl);
        res.addProperty(semGuid, UUID.randomUUID().toString());
        rl.addProperty(SKOSXL.literalForm, "My Test Concept 2", "en");
        rl.addProperty(RDF.type, SKOSXL.Label);
        res.addProperty(SKOS.broader, res);
        sr.addProperty(SKOS.hasTopConcept, res);

        // this test should error out unless check constraints are disabled...
        boolean resultIsSuccessful = client.commit();
        if (!resultIsSuccessful) {
          System.out.println("Failed as expected due to concept being a broader of itself.");
          System.out.println("commit failed, errors:");
          JobResult rec = client.getCommitJobResult();
          System.out.println("Job Id          : " + rec.jobId());
          System.out.println("HTTP status code: " + rec.httpStatusCode());
          System.out.println("errors          :");
          rec.errors().forEach( err -> {
            System.out.println("          type: " + err.errorType());
            System.out.println("   error level: " + err.params().errorLevel());
            System.out.println(" constraint id: " + err.params().constraintId());
            System.out.println("       message: " + err.params().message());
            System.out.println("          root: " + err.params().root());
          });
        } else {
          throw new RuntimeException("Should have failed on constraint violation!");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void secondLoopShouldFailTest() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("studiourl"));
      endpoint.setModelIRI(config.getProperty("modeluri"));
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Resource sr = m.createResource("http://myexample.com/Scheme4");
        Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");

        Resource res3 = m.createResource("http://myexample.com/MyTestConcept3");
        Resource res3l = m.createResource("http://myexample.com/MyTestConcept3_prefLabel_en");
        res3.addProperty(RDF.type, SKOS.Concept);
        res3.addProperty(SKOSXL.prefLabel, res3l);
        res3.addProperty(semGuid, UUID.randomUUID().toString());
        res3l.addProperty(SKOSXL.literalForm, "My Test Concept 3", "en");
        res3l.addProperty(RDF.type, SKOSXL.Label);
        sr.addProperty(SKOS.hasTopConcept, res3);

        Resource res4 = m.createResource("http://myexample.com/MyTestConcept3");
        Resource res4l = m.createResource("http://myexample.com/MyTestConcept3_prefLabel_en");
        res4.addProperty(RDF.type, SKOS.Concept);
        res4.addProperty(SKOSXL.prefLabel, res4l);
        res4.addProperty(semGuid, UUID.randomUUID().toString());
        res4l.addProperty(SKOSXL.literalForm, "My Test Concept 4", "en");
        res4l.addProperty(RDF.type, SKOSXL.Label);
        sr.addProperty(SKOS.hasTopConcept, res4);

        res3.addProperty(SKOS.broader, res4);
        res4.addProperty(SKOS.broader, res3);

        // this test should error out unless check constraints are disabled...
        boolean resultIsSuccessful = client.commit();
        if (!resultIsSuccessful) {
          System.out.println("Failed as expected due to two concepts creating a cycle in the hierarchy.");
          System.out.println("commit failed, errors:");
          JobResult rec = client.getCommitJobResult();
          System.out.println("Job Id          : " + rec.jobId());
          System.out.println("HTTP status code: " + rec.httpStatusCode());
          System.out.println("errors          :");
          rec.errors().forEach( err -> {
            System.out.println("          type: " + err.errorType());
            System.out.println("   error level: " + err.params().errorLevel());
            System.out.println(" constraint id: " + err.params().constraintId());
            System.out.println("       message: " + err.params().message());
            System.out.println("          root: " + err.params().root());
          });
        } else {
          throw new RuntimeException("Should have failed on constraint violation!");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void bigBeautifulYuugeBulkAddTest() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("studiourl"));
      endpoint.setModelIRI(config.getProperty("modeluri"));
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        client.getSparqlUpdateOptions().setRunEditRules(false);
        client.getSparqlUpdateOptions().setRunCheckConstraints(false);
        client.getSparqlUpdateOptions().setAcceptWarnings(true);
        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Resource sr = m.createResource("http://myexample.com/Scheme4");
        Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");

        for (int i = 1; i <= 1000; i++) {

          Resource res = m.createResource("http://myexample.com/MyTestConcept" + i);
          Resource resl = m.createResource("http://myexample.com/MyTestConcept" + i + "_prefLabel_en");
          res.addProperty(RDF.type, SKOS.Concept);
          res.addProperty(SKOSXL.prefLabel, resl);
          res.addProperty(semGuid, UUID.randomUUID().toString());
          resl.addProperty(SKOSXL.literalForm, "My Test Concept " + i, "en");
          resl.addProperty(RDF.type, SKOSXL.Label);
          sr.addProperty(SKOS.hasTopConcept, res);
        }

        // this test should error out unless check constraints are disabled...
        boolean resultIsSuccessful = client.commit();
        if (!resultIsSuccessful) {
          System.out.println("Failed as expected due to two concepts creating a cycle in the hierarchy.");
          System.out.println("commit failed, errors:");
          JobResult rec = client.getCommitJobResult();
          System.out.println("Job Id          : " + rec.jobId());
          System.out.println("HTTP status code: " + rec.httpStatusCode());
          System.out.println("errors          :");
          rec.errors().forEach(err -> {
            System.out.println("          type: " + err.errorType());
            System.out.println("   error level: " + err.params().errorLevel());
            System.out.println(" constraint id: " + err.params().constraintId());
            System.out.println("       message: " + err.params().message());
            System.out.println("          root: " + err.params().root());
          });
        } else {
          System.out.println("Big bad batch completed successfully!");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void bigWithBatchTest() {
    try {
      Properties config = TestConfig.getConfig();
      OEModelEndpoint endpoint = new OEModelEndpoint();
      endpoint.setBaseUrl(config.getProperty("studiourl"));
      endpoint.setModelIRI(config.getProperty("modeluri"));
      endpoint.setAccessToken(config.getProperty("accesstoken"));
      try (OEBatchClient client = new OEBatchClient(endpoint)) {
        client.getSparqlUpdateOptions().setRunEditRules(false);
        client.getSparqlUpdateOptions().setRunCheckConstraints(false);
        client.getSparqlUpdateOptions().setAcceptWarnings(true);
        client.setBatchEnabled(true);
        client.setBatchThreshold(1000);
        client.loadCurrentModelFromOE();
        Model m = client.getPendingModel();

        Resource sr = m.createResource("http://myexample.com/Scheme4");
        Property semGuid = m.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");

        for (int i = 1; i <= 1000; i++) {

          Resource res = m.createResource("http://myexample.com/SecondSetOfConcepts" + i);
          Resource resl = m.createResource("http://myexample.com/SecondSetOfConcepts" + i + "_prefLabel_en");
          res.addProperty(RDF.type, SKOS.Concept);
          res.addProperty(SKOSXL.prefLabel, resl);
          res.addProperty(semGuid, UUID.randomUUID().toString());
          resl.addProperty(SKOSXL.literalForm, "Second Set of Concepts " + i, "en");
          resl.addProperty(RDF.type, SKOSXL.Label);
          sr.addProperty(SKOS.hasTopConcept, res);
        }

        // this test should error out unless check constraints are disabled...
        boolean resultIsSuccessful = client.commit();
        if (!resultIsSuccessful) {
          System.out.println("Failed as expected due to two concepts creating a cycle in the hierarchy.");
          System.out.println("commit failed, errors:");
          JobResult rec = client.getCommitJobResult();
          System.out.println("Job Id          : " + rec.jobId());
          System.out.println("HTTP status code: " + rec.httpStatusCode());
          System.out.println("errors          :");
          rec.errors().forEach(err -> {
            System.out.println("          type: " + err.errorType());
            System.out.println("   error level: " + err.params().errorLevel());
            System.out.println(" constraint id: " + err.params().constraintId());
            System.out.println("       message: " + err.params().message());
            System.out.println("          root: " + err.params().root());
          });
        } else {
          System.out.println("Big bad batch completed successfully!");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

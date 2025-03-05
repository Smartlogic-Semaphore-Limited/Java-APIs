package com.smartlogic;

import com.smartlogic.oebatch.beans.JobResult;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

public class ExampleCloudClient {

	/**
	 * Logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(ModelLoader.class);

	/**
	 * Model should already exist in cloud tenancy configured.
	 */
	private final static String modelUri = "model:CloudTestModel";

	public static void main(String[] args) {
		//local(args);
		cloud(args);
	}

	public static void local(String[] args) {
		try {
			logger.info("Example 5.x client that downloads target model from local KMM instance, then does a small update.");
			Properties config = TestConfig.getConfig();
			OEModelEndpoint endpoint = new OEModelEndpoint();
			endpoint.setBaseUrl(config.getProperty("studiourl"));
			endpoint.setAccessToken(config.getProperty("accesstoken"));
			//endpoint.setCloudAPIKey(config.getProperty("apikey"));
			endpoint.setModelIRI(modelUri);
			try (OEBatchClient client = new OEBatchClient(endpoint)) {
				client.getSparqlUpdateOptions().runCheckConstraints = false;
				client.getSparqlUpdateOptions().runEditRules = false;
				client.getSparqlUpdateOptions().acceptWarnings = true;
				client.loadCurrentModelFromOE();
				Model pm = client.getPendingModel();
				Model cm = client.getCurrentModel();
				if (pm.size() != cm.size()) {
					logger.error("Invalid batch client, sizes are different: {} <-> {} ", cm.size(), pm.size());
				} else {
					logger.info("Pending and current model sizes match: {} triples (statements)", cm.size());
				}

/*
				pm.add(pm.createStatement(pm.createResource("urn:steveb:resource"), pm.createProperty("urn:steveb:property"), pm.createLiteral("Test 1")));
				pm.add(pm.createStatement(pm.createResource("urn:steveb:resource"), pm.createProperty("urn:steveb:property"), pm.createLiteral("Test 2")));
				pm.add(pm.createStatement(pm.createResource("urn:steveb:resource"), pm.createProperty("urn:steveb:property"), pm.createLiteral("Test 3", "de")));
				client.commit();
*/

			} finally {

			}
			logger.info("Client example completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("Example tests completed");
	}

	public static void cloud(String[] args) {
		try {
			System.out.println("Example 5.x client that downloads target model from cloud KMM instance.");
			Properties config = TestConfig.getCloudConfig();
			OEModelEndpoint endpoint = new OEModelEndpoint();
			endpoint.setBaseUrl(config.getProperty("studiourl"));
			endpoint.setCloudTokenFetchUrl(config.getProperty("tokenfetchurl"));
			endpoint.setCloudAPIKey(config.getProperty("apikey"));
			endpoint.setModelIRI(modelUri);
			try (OEBatchClient client = new OEBatchClient(endpoint)) {
				client.getSparqlUpdateOptions().runCheckConstraints = false;
				client.getSparqlUpdateOptions().runEditRules = false;
				client.getSparqlUpdateOptions().acceptWarnings = true;
				client.loadCurrentModelFromOE();
				Model pm = client.getPendingModel();
				Model cm = client.getCurrentModel();
				if (pm.size() != cm.size()) {
					logger.error("Invalid batch client, sizes are different: {} <-> {} ", cm.size(), pm.size());
				} else {
					logger.info("Pending and current model sizes match: {} triples (statements)", cm.size());
				}

				/* add a scheme and a concept via batch update */
				Resource sr = pm.createResource("http://myexample.com/Scheme1");
				sr.addProperty(RDF.type, SKOS.ConceptScheme);
				sr.addProperty(RDFS.label, pm.createLiteral("TestScheme", "en"));
				Property semGuid = pm.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
				Resource res = pm.createResource("http://myexample.com/TestConcept1");
				Resource resl = pm.createResource("http://myexample.com/TestConcept1_prefLabel_en");
				res.addProperty(RDF.type, SKOS.Concept);
				res.addProperty(SKOSXL.prefLabel, resl);
				res.addProperty(semGuid, UUID.randomUUID().toString());
				resl.addProperty(SKOSXL.literalForm, "Example Concept 1", "en");
				resl.addProperty(RDF.type, SKOSXL.Label);
				sr.addProperty(SKOS.hasTopConcept, res);

				boolean resultIsSuccessful = client.commit();
				if (!resultIsSuccessful) {
					System.out.println("Basic PDC cloud test failed.");
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
					System.out.println("Commit completed successfully!");
				}
			}
			System.out.println("Cloud client example completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("All example tests completed");
	}

}

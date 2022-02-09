package com.smartlogic;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ExampleCloudClient {

	/**
	 * Logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(ModelLoader.class);

	private final static String modelUri = "model:SpaceMissions";

	public static void main(String[] args) {
		//local(args);
		cloud(args);
	}

	public static void local(String[] args) {
		try {
			logger.info("Example 5.x client that downloads Space Missions model from local KMM instance.");
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
			System.out.println("Example 5.x client that downloads Space Missions model from cloud KMM instance.");
			Properties config = TestConfig.getCloudConfig();
			OEModelEndpoint endpoint = new OEModelEndpoint();
			endpoint.setBaseUrl(config.getProperty("studiourl"));
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

			} finally {

			}
			System.out.println("Client example completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Example tests completed");
	}

}

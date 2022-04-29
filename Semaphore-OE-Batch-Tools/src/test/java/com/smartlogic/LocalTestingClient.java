package com.smartlogic;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LocalTestingClient {

	/**
	 * Logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(LocalTestingClient.class);

	public static final Property semGuidProp;
	public static final Resource conceptSchemeRes;

	static {
		Model tempModel = ModelFactory.createDefaultModel();
		semGuidProp = tempModel.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
		conceptSchemeRes = tempModel.createResource("http://example.com/BatchTest#ConceptScheme/ConceptScheme");
	}

	public static void main(String[] args) {
		try {
			OEModelEndpoint endpoint = new OEModelEndpoint();
			endpoint.setAccessToken("WyJHb2QiLDE5NjE5NTA1NTIsWyJTZW1hcGhvcmVBdXRoZW50aWNhdGVkVXNlcnMiLCJTZW1hcGhvcmVTdXBlckFkbWluaXN0cmF0b3JzIl0sIk1FVUNJR1A5bXVWcEVISm16V2pTZm5ZK0IvZGM5Y2l3VnZnZ2xBQTBoT2lobWRWd0FpRUE2Vm8vSVZrblkySU9BdExHSjlOOUdXUTdjSVN4bUpNc2g1ejlPaVgrZzNVPSJd");
			endpoint.setBaseUrl("http://localhost:5080");
			endpoint.setModelIRI("model:BatchTest");
			try (OEBatchClient client = new OEBatchClient(endpoint)) {
				client.getSparqlUpdateOptions().runCheckConstraints = false;
				client.getSparqlUpdateOptions().runEditRules = false;
				client.getSparqlUpdateOptions().acceptWarnings = true;
				client.loadCurrentModelFromOE();
				Model m = client.getPendingModel();

				logger.info("Model size: {}", m.size());

				client.setBatchThreshold(50000);

				addConcepts(m);
//				deleteConcepts(m);

				client.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteConcepts(Model m) {

		Set<Resource> conceptResources = Sets.newHashSet();
		Set<Resource> conceptLabelResources = Sets.newHashSet();

		{
			StmtIterator it = m.listStatements(null, RDF.type, SKOS.Concept);
			while (it.hasNext()) {
				Statement stmt = it.nextStatement();
				conceptResources.add(stmt.getSubject());
			}
		}
		{
			StmtIterator it = m.listStatements(null, RDF.type, SKOSXL.Label);
			while (it.hasNext()) {
				Statement stmt = it.nextStatement();
				conceptLabelResources.add(stmt.getSubject());
			}
		}

		List<Statement> toDelete = Lists.newArrayList();

		{
			conceptResources.forEach(c -> {
				toDelete.add(m.createStatement(conceptSchemeRes, SKOS.hasTopConcept, c));
				StmtIterator it = m.listStatements(c, null, (RDFNode) null);
				while (it.hasNext()) {
					Statement stmt = it.nextStatement();
					toDelete.add(stmt);
				}
			});
		}
		{
			conceptLabelResources.forEach(cl -> {
				StmtIterator it = m.listStatements(cl, null, (RDFNode) null);
				while (it.hasNext()) {
					Statement stmt = it.nextStatement();
					toDelete.add(stmt);
				}
			});
		}
		m.remove(toDelete);
	}

	public static void addConcepts(Model m) {
		for ( int i = 0; i < 54231; i++) {
			addConcept(m, i);
		}
	}

	public static void addConcept(Model m, int i) {
		String name = "Term " + i;
		String guid = UUID.randomUUID().toString();
		String uri = String.format("http://example.com/BatchTest#%s", guid);
		Resource conceptRes = m.createResource(uri);
		Resource conceptSchemeRes = m.createResource("http://example.com/BatchTest#ConceptScheme/ConceptScheme");
		m.add(m.createStatement(conceptSchemeRes, SKOS.hasTopConcept, conceptRes));
		m.add(m.createStatement(conceptRes, RDF.type, SKOS.Concept));
		m.add(m.createStatement(conceptRes, semGuidProp, guid));
		Resource labelRes = m.createResource(uri + "_prefLabel_en");
		m.add(m.createStatement(conceptRes, SKOSXL.prefLabel, labelRes));
		m.add(m.createStatement(labelRes, RDF.type, SKOSXL.Label));
		Literal lit = m.createLiteral(name, "en");
		m.add(m.createStatement(labelRes, SKOSXL.literalForm, lit));
	}
}

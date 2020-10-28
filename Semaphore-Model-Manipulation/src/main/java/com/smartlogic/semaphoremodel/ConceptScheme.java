package com.smartlogic.semaphoremodel;

import org.apache.commons.compress.utils.Sets;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

import java.util.Set;

public class ConceptScheme extends IdentifiableObject {

	public ConceptScheme(Model model, Resource resource) {
		super(model, resource);
	}

	/**
	 * Add a Concept as a top concept for this ConceptScheme.
	 *
	 * @param concept
	 */
	public void addTopConcept(Concept concept) {
		resource.addProperty(SKOS.hasTopConcept, concept.getResource());
		concept.getResource().addProperty(SKOS.topConceptOf, resource);
	}

	/**
	 * Return the set of top concept objects.
	 *
	 * @return
	 */
	public Set<Concept> getTopConcepts() {
		Set<Concept> topConcepts = Sets.newHashSet();
		ParameterizedSparqlString pSparql = new ParameterizedSparqlString();
		pSparql.setCommandText("PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"select ?tc where { ?conceptSchemeUri skos:hasTopConcept ?tc . }");
		pSparql.setIri("conceptSchemeUri", resource.getURI());
		QueryExecution qe = QueryExecutionFactory.create(pSparql.asQuery(), model);
		ResultSet rs = qe.execSelect();
		while (rs.hasNext()) {
			topConcepts.add(new Concept(model, rs.next().getResource("tc")));
		}
		return topConcepts;
	}
}

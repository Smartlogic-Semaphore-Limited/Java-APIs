package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.SKOS;

public class ConceptScheme extends ObjectWithURI {

	public ConceptScheme(Model model, Resource resource) {
		super(model, resource);
	}

	public void addTopConcept(Concept concept) {
		resource.addProperty(SKOS.hasTopConcept, concept.getResource());
		concept.getResource().addProperty(SKOS.topConceptOf, resource);
	}

}

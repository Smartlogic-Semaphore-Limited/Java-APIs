package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class HasBroaderRelationshipType extends ConceptToConceptRelationshipType {

	protected HasBroaderRelationshipType(Model model, Resource resource, Resource inverseResource) {
		super(model, resource, inverseResource);
	}

}

package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class AssocativeRelationshipType extends ConceptToConceptRelationshipType {

	public AssocativeRelationshipType(Model model, Resource resource, Resource inverseResource) {
		super(model, resource, inverseResource);
	}

	public AssocativeRelationshipType(Model model, Resource resource) {
		super(model, resource, null);
	}

}

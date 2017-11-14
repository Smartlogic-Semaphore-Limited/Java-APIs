package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public abstract class ConceptToConceptRelationshipType extends RelationshipType {

	public ConceptToConceptRelationshipType(Model model, Resource resource, Resource inverseResource) {
		super(model, resource, inverseResource);
	}

}

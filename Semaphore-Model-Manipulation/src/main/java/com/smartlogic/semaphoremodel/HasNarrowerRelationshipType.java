package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class HasNarrowerRelationshipType extends ConceptToConceptRelationshipType {

	protected HasNarrowerRelationshipType(Model model, Resource resource, Resource inverseResource) {
		super(model, resource, inverseResource);
	}

}

package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class HasEquivalentRelationshipType extends RelationshipType {

	protected HasEquivalentRelationshipType(Model model, Resource resource) {
		super(model, resource, null);
	}

}

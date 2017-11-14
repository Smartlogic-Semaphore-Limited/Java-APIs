package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public abstract class RelationshipType extends ObjectWithURI {
	
	private final Property property;
	private final Resource inverseResource;
	private final Property inverseProperty;
	
	protected RelationshipType(Model model, Resource resource, Resource inverseResource) {
		super(model, resource);
		property = model.createProperty(resource.getURI());
		
		this.inverseResource = inverseResource;
		if (inverseResource != null) this.inverseProperty = model.createProperty(inverseResource.getURI());
		else inverseProperty = null;
	}
	
	protected Property getInverseProperty() {
		return inverseProperty;
	}

	protected Resource getInverseResource() {
		return inverseResource;
	}

	protected Property getProperty() {
		return property;
	}
	
}


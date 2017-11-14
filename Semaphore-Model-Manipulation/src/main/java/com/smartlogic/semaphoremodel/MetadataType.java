package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public abstract class MetadataType extends ObjectWithURI {

	private final Property property;
	protected MetadataType(Model model, Resource resource) {
		super(model, resource);
		property = model.createProperty(resource.getURI());
	}
	
	protected Property getProperty() {
		return property;
	}

}

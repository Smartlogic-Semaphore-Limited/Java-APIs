package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.impl.StatementImpl;
import org.apache.jena.vocabulary.RDF;

public abstract class MetadataType extends ObjectWithURI {

	private final Property property;
	protected MetadataType(Model model, Resource resource) {
		super(model, resource);
		property = model.createProperty(resource.getURI());
	}
	
	protected Property getProperty() {
		return property;
	}
	
	public void setAlwaysVisibleProperty() {
		resource.addProperty(RDF.type, SEM.alwaysVisibleProperty);
	}
	
	public void removeAlwaysVisibleProperty() {
		Statement statement = new StatementImpl(resource, RDF.type, SEM.alwaysVisibleProperty);
		model.remove(statement);
	}

	public boolean equals(Object otherObject) {
		if (!this.getClass().equals(otherObject.getClass())) return false;
		
		MetadataType otherMetadataType = (MetadataType)otherObject;
		
		return this.getProperty().equals(otherMetadataType.getProperty());
	}

}

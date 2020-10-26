package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.*;

import java.util.Collection;
import java.util.HashSet;

public abstract class ConceptObject extends ObjectWithURI {

	protected ConceptObject(Model model, Resource resource) {
		super(model, resource);
	}

	public Collection<ConceptObject> getRelated(Property relationship) {
		Collection<ConceptObject> returnData = new HashSet<ConceptObject>();
		StmtIterator stmtIterator = resource.listProperties(relationship);
		
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			Resource relatedResource = statement.getObject().asResource();
			returnData.add(new Concept(model, relatedResource));
		}
		return returnData;
	}
	
	public Collection<ConceptObject> getRelated(RelationshipType relationshipType) {
		Collection<ConceptObject> returnData = new HashSet<ConceptObject>();
		StmtIterator stmtIterator = resource.listProperties(relationshipType.getProperty());
		
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			Resource relatedResource = statement.getObject().asResource();
			returnData.add(new Concept(model, relatedResource));
		}
		return returnData;
	}




	

}

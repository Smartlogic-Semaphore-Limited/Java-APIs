package com.smartlogic.semaphoremodel;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.SKOS;

public abstract class ConceptObject extends ObjectWithURI {

	protected ConceptObject(Model model, Resource resource) {
		super(model, resource);
	}

	/**
	 * Return the guid of this concept object
	 * @return - the guid of the object
	 */
	public String getGuid() {
		return getIdentifier(SEM.guid);
	}

	private String getIdentifier(Property identifierProperty) {
		Statement statement = resource.getProperty(identifierProperty);
		if (statement == null) return null;
		return statement.getObject().asLiteral().getString();
	}
	
	public Collection<ConceptObject> getRelated(Property relationship) {
		Collection<ConceptObject> returnData = new HashSet<ConceptObject>();
		StmtIterator stmtIterator = resource.listProperties(relationship);
		
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			Resource relatedResource = statement.getObject().asResource();
			if (relationship.equals(SKOS.topConceptOf)) {
				returnData.add(new ConceptScheme(model, relatedResource));
			} else {
				returnData.add(new Concept(model, relatedResource));
			}
		}
		return returnData;
	}

	/**
	 * Add this guid to the concept object.
	 * If a guid is already present then a ModelException is thrown
	 * @param uuid - the UUID that is to be added to the model
	 * @throws ModelException - if there is already a guid present
	 */
	public void addGuid(UUID uuid) throws ModelException {
		addIdentifier(SEM.guid, createLiteral(uuid));
	}

	public void addIdentifier(Property identifierProperty, Literal literal) throws ModelException {
		if (resource.hasProperty(identifierProperty)) throw new ModelException("Concept %s already has an identifier present", resource.getURI());
		resource.addProperty(identifierProperty, literal);
	}

	/**
	 * Set the guid of the concept object to this value.
	 * We don't care what the previous value was
	 * @param uuid - the UUID to be set on the model
	 */
	public void setGuid(UUID uuid) {
		setIdentifier(SEM.guid, createLiteral(uuid));
	}

	/**
	 * set the identifier to the supplied value
	 * @param identifierProperty - the identifier property to use
	 * @param literal - the new value for this identifier
	 */
	public void setIdentifier(Property identifierProperty, Literal literal) {
		resource.removeAll(identifierProperty);
		resource.addProperty(identifierProperty, literal);
	}
	
	/**
	 * Update the guid to the supplied value.
	 * If there was not a value already present an exception is thrown
	 * @param uuid - the new value for the UUID
	 * @throws ModelException - thrown if there is not already a UUID present
	 */
	public void updateGuid(UUID uuid) throws ModelException {
		updateIdentifier(SEM.guid, createLiteral(uuid));
	}

	private void updateIdentifier(Property identifierProperty, Literal literal) throws ModelException {
		if (!resource.hasProperty(identifierProperty)) throw new ModelException("Attempting to update non-existent identifier of %s", resource.getURI());
		setIdentifier(identifierProperty, literal);
	}

	/**
	 * Remove the supplied guid fom the concept object.
	 * We don't care what the state of the guid was before this operation 
	 * @param uuid - the UUID to be removed
	 */
	public void removeGuid(UUID uuid) {
		clearIdentifier(SEM.guid);
	}

	/**
	 * Remote the supplied guid from the concept object.
	 * If the guid was not previously present, then an exception is thrown
	 * @param uuid - the UUID to be removed
	 * @throws ModelException if the UUID isn't present to be deleted
	 */
	public void deleteGuid(UUID uuid) throws ModelException {
		deleteIdentifier(SEM.guid, createLiteral(uuid));
	}

	private void deleteIdentifier(Property identifierProperty, Literal literal) throws ModelException {
		if (!resource.hasProperty(identifierProperty, literal)) throw new ModelException("Attempting to delete non-existent identifier '%s' of %s", literal.getString(), resource.getURI());
		clearIdentifier(identifierProperty);
	}


	/**
	 * Clear any guid from the concept object
	 * We don't care about the state of the guid before the operation 
	 */
	public void clearGuid() {
		clearIdentifier(SEM.guid);
	}

	private void clearIdentifier(Property identifierProperty) {
		resource.removeAll(identifierProperty);
	}

	private Literal createLiteral(UUID uuid) {
		return model.createLiteral(uuid.toString(), "");
	}
	





	

}

package com.smartlogic.semaphoremodel;

import java.util.UUID;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public abstract class ConceptObject extends ObjectWithURI {

	protected ConceptObject(Model model, Resource resource) {
		super(model, resource);
	}

	/**
	 * Return the guid of this concept object
	 * @return
	 */
	public String getGuid() {
		return getIdentifier(SEM.guid);
	}

	private String getIdentifier(Property identifierProperty) {
		Statement statement = resource.getProperty(identifierProperty);
		if (statement == null) return null;
		return statement.getObject().asLiteral().getString();
	}

	/**
	 * Add this guid to the concept object.
	 * If a guid is already present then a ModelException is thrown
	 * @throws ModelException
	 */
	public void addGuid(UUID uuid) throws ModelException {
		addIdentifier(SEM.guid, createLiteral(uuid));
	}

	public void addIdentifier(Property identifierProperty, Literal literal) throws ModelException {
		if (resource.hasProperty(identifierProperty)) throw new ModelException("Concept %s already has a GUID present", resource.getURI());
		resource.addProperty(identifierProperty, literal);
	}

	/**
	 * Set the guid of the concept object to this value.
	 * We don't care what the previous value was
	 */
	public void setGuid(UUID uuid) {
		setIdentifier(SEM.guid, createLiteral(uuid));
	}

	public void setIdentifier(Property identifierProperty, Literal literal) {
		resource.removeAll(identifierProperty);
		resource.addProperty(identifierProperty, literal);
	}
	
	/**
	 * Update the guid to the supplied value.
	 * If there was not a value already present an exception is thrown
	 * @param uuid
	 * @throws ModelException
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
	 * @param uuid
	 * @throws ModelException 
	 */
	public void removeGuid(UUID uuid) {
		clearIdentifier(SEM.guid);
	}

	/**
	 * Remote the supplied guid from the concept object.
	 * If the guid was not previously present, then an exception is thrown
	 * @param uuid
	 * @throws ModelException 
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

package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

import java.util.UUID;

/**
 * Abstract class for identifiable resources, which means
 * they can have one Semaphore GUID and optional an identifier
 * of the user's choice.
 */
public abstract class IdentifiableObject extends ObjectWithURI {

	/**
	 * Constructor requires a model and an resource.
	 *
	 * @param model
	 * @param resource
	 */
	protected IdentifiableObject(Model model, Resource resource) {
		super(model, resource);
	}

	/**
	 * Return the guid of this object
	 *
	 * @return - the guid of the object
	 */
	public String getGuid() {
		return selectPropertyLiteralString(SEM.guid);
	}

	/**
	 * Set the guid of the concept object to this value.
	 *
	 * @param uuid - the UUID to be set on the model
	 */
	public void setGuid(UUID uuid) {
		deletePropertyLiterals(SEM.guid);
		insertPropertyLiteral(SEM.guid, createLiteralFromUUID(uuid));
	}

	/**
	 * Update the guid to the supplied value. Inserts value if no guid specified.
	 *
	 * @param uuid - the new value for the UUID
	 */
	public void updateGuid(UUID uuid) {
		deletePropertyLiterals(SEM.guid);
		insertPropertyLiteral(SEM.guid, createLiteralFromUUID(uuid));
	}

	/**
	 * Remote the supplied guid from the concept object.
	 *
	 * @param uuid - the UUID to be removed
	 */
	public void deleteGuid(UUID uuid) throws ModelException {
		if (!propertyLiteralExists(SEM.guid, createLiteralFromUUID(uuid)))
			throw new ModelException("Attempted to delete a GUID that does not exist for this resource.");
		deletePropertyLiteral(SEM.guid, createLiteralFromUUID(uuid));
	}

	/**
	 * Clear any guid from the concept object
	 * We don't care about the state of the guid before the operation
	 */
	public void clearGuid() {
		deletePropertyLiterals(SEM.guid);
	}

	public String getIdentifier(Property identifierProperty) {
		return selectPropertyLiteralString(identifierProperty);
	}

	public void addIdentifier(Property identifierProperty, Literal literal) throws ModelException {
		if (resource.hasProperty(identifierProperty))
			throw new ModelException("Concept %s already has an identifier present", resource.getURI());
		resource.addProperty(identifierProperty, literal);
	}

	/**
	 * set the identifier to the supplied value
	 *
	 * @param identifierProperty - the identifier property to use
	 * @param literal            - the new value for this identifier
	 */
	public void setIdentifier(Property identifierProperty, Literal literal) {
		resource.removeAll(identifierProperty);
		resource.addProperty(identifierProperty, literal);
	}

	public void updateIdentifier(Property identifierProperty, Literal literal) throws ModelException {
		if (!resource.hasProperty(identifierProperty))
			throw new ModelException("Attempting to update non-existent identifier of %s", resource.getURI());
		setIdentifier(identifierProperty, literal);
	}

	protected void deleteIdentifier(Property identifierProperty, Literal literal) throws ModelException {
		if (!resource.hasProperty(identifierProperty, literal))
			throw new ModelException("Attempting to delete non-existent identifier '%s' of %s", literal.getString(), resource.getURI());
		clearIdentifier(identifierProperty);
	}

	protected void clearIdentifier(Property identifierProperty) {
		resource.removeAll(identifierProperty);
	}

	protected Literal createLiteralFromUUID(UUID uuid) {
		return model.createLiteral(uuid.toString(), "");
	}


}


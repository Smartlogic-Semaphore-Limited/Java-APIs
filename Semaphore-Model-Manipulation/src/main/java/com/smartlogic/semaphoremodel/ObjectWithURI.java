package com.smartlogic.semaphoremodel;

import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDFS;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public abstract class ObjectWithURI {

	protected final Model model;
	protected final Resource resource;
	
	protected ObjectWithURI(Model model, Resource resource) {
		this.model = model;
		this.resource = resource;
	}
	
	/**
	 * Make this protected - users of the library shouldn't use this. 
	 * @return The resource containing the URI for this object
	 */
	protected Resource getResource() {
		return resource;
	}


	/**
	 * Returns the URI for this object.
	 * @return the URI for this object.
	 */
	public URI getURI() {
		if (resource == null)
			return null;
		return URI.create(resource.getURI());
	}
	/**
	 * Return the label for this object in the selected language
	 * @param language - the language for which the label is requested
	 * @return - the label in the requested language.
	 * @throws ModelException - thrown if there are multiple labels present for this object in this language
	 */
	public Label getLabel(Language language) throws ModelException {
		String sparql = "SELECT ?label WHERE { ?objectURI rdfs:label ?label . FILTER(LANG(?label) = STR(?labelLanguage)) }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("objectURI", resource);
		parameterizedSparql.setParam("labelLanguage",
				(language == null ? model.createLiteral("") : model.createLiteral(language.getCode(), "")));
		
		Query query = QueryFactory.create(parameterizedSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = qexec.execSelect();
		
		if (!resultSet.hasNext()) return null;
		
		QuerySolution querySolution = resultSet.next();
		Label label = new Label(querySolution.getLiteral("label"));
		
		if (!resultSet.hasNext()) return label;
		
		throw new ModelException("%s has more than one label in language '%s'", resource.getURI(), language.getCode());
	}


	/**
	 * addLabel
	 * Adds the label - if there is already a label of this language an exception is thrown.
	 * @param label - the label to be added to the object
	 * @throws ModelException - thrown if some model constraint would be broken by this action 
	 */
	public void addLabel(Label label) throws ModelException {
		checkLabelDoesntExistInLanguage(label);
		
		addLabelPostCheck(label);
	}
	
	/**
	 * setLabel
	 * After this operation, the object will have this label and no other in this language.
	 * It does not matter whether there already was a label of this language present.
	 * @param label - the label to be added to the object
	 * @throws ModelException - thrown if some model constraint would be broken by this action 
	 */
	public void setLabel(Label label) throws ModelException {
		deleteLabelForLanguagePostCheck(label);
		addLabelPostCheck(label);
	}

	/**
	 * updateLabel
	 * Delete the current label in this language and replace it with the supplied one
	 * If there was no label in this language, then throw an exception
	 * @param label - the label to be updated for the object
	 * @throws ModelException - thrown if some model constraint would be broken by this action 
	 */
	public void updateLabel(Label label) throws ModelException {
		checkLabelExistsInLanguage(label);

		deleteLabelForLanguagePostCheck(label);
		addLabelPostCheck(label);
	}
	
	/**
	 * Delete the supplied label from this object.
	 * If the label is not present on the object then an exception will be thrown.
	 * @param label - the label to be deleted from the object
	 * @throws ModelException - thrown if some model constraint would be broken by this action 
	 */
	public void deleteLabel(Label label) throws ModelException {
		checkLabelExists(label);
		
		deleteLabelPostCheck(label);
	}
	
	/**
	 * Delete the supplied label from this object.
	 * This doesn't care whether the label originally existed
	 * @param label - the label to be removed from the object
	 * @throws ModelException - thrown if some model constraint would be broken by this action 
	 */
	public void removeLabel(Label label) throws ModelException {
		
		deleteLabelPostCheck(label);
	}
	
	private void deleteLabelForLanguagePostCheck(Label label) {
		String sparql =  "DELETE { ?objectURI rdfs:label ?labelValue } WHERE { ?objectURI rdfs:label ?labelValue . FILTER(LANG(?labelValue) = STR(?labelLanguage)) }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("objectURI", resource);
		parameterizedSparql.setParam("labelLanguage", model.createLiteral(label.getLanguageCode(), ""));
		
		UpdateAction.execute(parameterizedSparql.asUpdate(), model);
	}
	
	private void deleteLabelPostCheck(Label label) {
		String sparql = "DELETE { ?objectURI rdfs:label ?labelValue } WHERE { ?objectURI rdfs:label ?labelValue . FILTER (?labelValue = ?suppliedLabel) }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("objectURI", resource);
		parameterizedSparql.setParam("suppliedLabel", model.createLiteral(label.getValue(), label.getLanguageCode()));
		
		UpdateAction.execute(parameterizedSparql.asUpdate(), model);
	}

	
	private void addLabelPostCheck(Label label) {
		this.getResource().addProperty(RDFS.label, SemaphoreModel.getAsLiteral(model, label));
	}

	private void checkLabelExistsInLanguage(Label label) throws ModelException {
		if (!labelExistsInLanguage(label)) throw new ModelException("Attempting to delete label for '%s'. This already has no label in language '%s'", resource.getURI(), label.getLanguageCode());
	}

	private void checkLabelDoesntExistInLanguage(Label label) throws ModelException {
		if (labelExistsInLanguage(label)) throw new ModelException("Attempting to create label for '%s'. This already has a label in language '%s'", resource.getURI(), label.getLanguageCode());
	}

	private boolean labelExistsInLanguage(Label label) {
		String sparql = "ASK WHERE { ?objectURI rdfs:label ?label . FILTER(LANG(?label) = STR(?labelLanguage)) }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("objectURI", resource);
		parameterizedSparql.setParam("labelLanguage", model.createLiteral(label.getLanguageCode(), ""));
		
		Query query = QueryFactory.create(parameterizedSparql.asQuery());

		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			return qexec.execAsk();
		}
	}

	private void checkLabelExists(Label label) throws ModelException {
		if (!labelExists(label)) throw new ModelException("Attempting to delete label %s for '%s'. This label does not exist", label.toString(), resource.getURI());
	}

	@SuppressWarnings("unused")
	private void checkLabelDoesntExist(Label label) throws ModelException {
		if (labelExists(label)) throw new ModelException("Attempting to create label for %s. This already has a label in language '%s'", resource.getURI(), label.getLanguageCode());
	}

	private boolean labelExists(Label label) {
		String sparql = "ASK WHERE { ?objectURI rdfs:label ?label }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("objectURI", resource);
		parameterizedSparql.setParam("label", model.createLiteral(label.getValue(), label.getLanguageCode()));
		
		Query query = QueryFactory.create(parameterizedSparql.asQuery());

		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			return qexec.execAsk();
		}
	}


	protected String getBase64(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes());
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
	 * Add this guid to the concept object.
	 * If a guid is already present then a ModelException is thrown
	 *
	 * @param uuid - the UUID that is to be added to the model
	 * @throws ModelException - if there is already a guid present
	 */
	public void addGuid(UUID uuid) throws ModelException {
		if (propertyLiteralExists(SEM.guid, createLiteralFromUUID(uuid)))
			throw new ModelException("Attempted to add the same GUID to the resource");
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
	 * Remove the supplied guid fom the concept object.
	 *
	 * @param uuid - the UUID to be removed
	 */
	public void removeGuid(UUID uuid) {
		deletePropertyLiteral(SEM.guid, createLiteralFromUUID(uuid));
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

	public boolean propertyLiteralExists(Property property, Literal literal) {
		StmtIterator it = model.listStatements(resource, property, (RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = it.nextStatement();
			Literal lit = stmt.getLiteral();
			if (lit.equals(literal))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the specified property and string value exists for this resource.
	 *
	 * @param property
	 * @param value
	 * @param lang
	 * @return
	 */
	public boolean propertyLiteralStringExists(Property property, String value, String lang) {
		StmtIterator it = model.listStatements(resource, property, (RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = it.nextStatement();
			Literal lit = stmt.getLiteral();
			if (!Strings.isNullOrEmpty(lang)) {
				if (lit.getString().equals(value) && lit.getLanguage().equalsIgnoreCase(lang))
					return true;
			} else {
				if (lit.getString().equals(value))
					return true;
			}
		}
		return false;
	}

	/**
	 * Delete all triples for this resource with this property.
	 *
	 * @param property
	 */
	public void deletePropertyLiterals(Property property) {
		List<Statement> toDelete = Lists.newArrayList();
		StmtIterator it = model.listStatements(resource, property, (RDFNode) null);
		while (it.hasNext()) {
			toDelete.add(it.nextStatement());
		}
		model.remove(toDelete);
	}

	/**
	 * Get a Literal value for the specified property. This method
	 * assumes there is only one triple with this resource and property
	 * (i.e. cardinality 1). If there is more than 1 triple with this property
	 * for this resource, a random triple will be returned.
	 *
	 * @param property
	 * @return
	 */
	public Literal selectPropertyLiteral(Property property) {
		Statement statement = resource.getProperty(property);
		if (statement == null) return null;
		return statement.getObject().asLiteral();
	}

	/**
	 * Return the set of literals for the specified property on this resource.
	 *
	 * @param property
	 * @return
	 */
	public Set<Literal> selectPropertyLiterals(Property property) {
		Set<Literal> lits = Sets.newHashSet();
		StmtIterator it = model.listStatements(resource, property, (RDFNode) null);
		while (it.hasNext()) {
			Statement stmt = it.nextStatement();
			lits.add(stmt.getObject().asLiteral());
		}
		return lits;
	}

	/**
	 * Get a property Literal string value for the specified property.
	 *
	 * @param property
	 * @return
	 */
	public String selectPropertyLiteralString(Property property) {
		Statement statement = resource.getProperty(property);
		if (statement == null) return null;
		return statement.getObject().asLiteral().getString();
	}

	/**
	 * Insert a property literal triple for this object.
	 *
	 * @param property
	 * @param literal
	 */
	public void insertPropertyLiteral(Property property, Literal literal) {
		model.add(model.createStatement(this.resource, property, literal));
	}

	/**
	 * Delete the property literal triple for this object.
	 *
	 * @param property
	 * @param literal
	 */
	public void deletePropertyLiteral(Property property, Literal literal) {
		Statement toDelete = null;
		StmtIterator it = model.listStatements(this.resource, property, literal);
		while (it.hasNext()) {
			toDelete = it.nextStatement();
		}
		if (toDelete != null)
			model.remove(toDelete);
	}

	/**
	 * Return the set of resources that are objects of the specified property.
	 *
	 * @param property
	 * @return
	 */
	public Set<Resource> selectPropertyResources(Property property) {
		Set<Resource> resources = Sets.newHashSet();
		StmtIterator it = model.listStatements(resource, property, (RDFNode) null);
		while (it.hasNext()) {
			resources.add(it.nextStatement().getObject().asResource());
		}
		return resources;
	}

	public Set<Concept> selectPropertyConcepts(Property property) {
		Set<Concept> concepts = Sets.newHashSet();
		StmtIterator it = model.listStatements(resource, property, (RDFNode) null);
		while (it.hasNext()) {
			concepts.add(new Concept(model, it.nextStatement().getObject().asResource()));
		}
		return concepts;
	}
}

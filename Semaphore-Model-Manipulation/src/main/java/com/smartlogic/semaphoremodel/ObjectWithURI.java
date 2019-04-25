package com.smartlogic.semaphoremodel;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDFS;
import org.apache.xerces.impl.dv.util.Base64;

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
		parameterizedSparql.setParam("labelLanguage", model.createLiteral(language.getCode(), ""));
		
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
		return Base64.encode(value.getBytes());
	}

}

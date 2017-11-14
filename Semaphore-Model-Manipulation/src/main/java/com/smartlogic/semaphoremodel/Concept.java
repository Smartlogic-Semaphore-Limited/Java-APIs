package com.smartlogic.semaphoremodel;

import java.util.Calendar;
import java.util.Date;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;

public class Concept extends ConceptObject {

	protected Concept(Model model, Resource resource) {
		super(model, resource);
	}

	
	public void addChildConcept(Concept childConcept) {
		resource.addProperty(SKOS.narrower, childConcept.getResource());
		childConcept.getResource().addProperty(SKOS.broader, resource);
	}
		
	public void addParentConcept(Concept childConcept) {
		resource.addProperty(SKOS.broader, childConcept.getResource());
		childConcept.getResource().addProperty(SKOS.narrower, resource);
	}

	public void addMetadata(StringMetadataType metadataType, String value, Language language) {
		resource.addProperty(metadataType.getProperty(), model.createLiteral(value, language.getCode()));
	}

	public void addMetadata(CalendarMetadataType metadataType, Calendar calendar) {
		resource.addProperty(metadataType.getProperty(), model.createTypedLiteral(calendar.getTime(), "xsd:Date"));
	}

	public void addMetadata(CalendarMetadataType metadataType, Date date) {
		resource.addProperty(metadataType.getProperty(), model.createTypedLiteral(date, "xsd:Date"));
	}

	public void addMetadata(BooleanMetadataType metadataType, boolean bool) {
		resource.addProperty(metadataType.getProperty(), model.createTypedLiteral(bool));
	}

	public void addRelation(RelationshipType relationshipType, Concept concept) {
		resource.addProperty(relationshipType.getProperty(), concept.getResource());
	}

	public void addAssociated(Concept concept) {
		resource.addProperty(SKOS.related, concept.getResource());
	}
	
	public void addParent(Concept concept) {
		resource.addProperty(SKOS.broader, concept.getResource());
	}
	public void addChild(Concept concept) {
		resource.addProperty(SKOS.narrower, concept.getResource());
	}

	public void addRelationship(ConceptToConceptRelationshipType relationshipType, Concept concept2) {
		resource.addProperty(relationshipType.getProperty(), concept2.getResource());
		if (relationshipType.getInverseProperty() == null) {
			concept2.getResource().addProperty(relationshipType.getProperty(), this.getResource()); // Symmetric property
		} else {
			concept2.getResource().addProperty(relationshipType.getInverseProperty(), this.getResource()); // Assymmetric property
		}
	}

	
	/**
	 * addLabel
	 * Adds the label - if there is already a label of this language an exception is thrown.
	 * @param label
	 * @throws ModelException
	 */
	public void addLabel(Label label) throws ModelException {
		checkLabelDoesntExistInLanguage(label);
		
		addLabelPostCheck(label);
	}
	
	/**
	 * setLabel
	 * After this operation, the Concept Scheme will have this label and no other in this language.
	 * It does not matter whether there already was a label of this language present.
	 * @param label
	 * @throws ModelException
	 */
	public void setLabel(Label label) throws ModelException {
		deleteLabelForLanguagePostCheck(label);
		addLabelPostCheck(label);
	}

	/**
	 * updateLabel
	 * Delete the current label in this language and replace it with the supplied one
	 * If there was no label in this language, then throw an exception
	 * @param label
	 * @throws ModelException
	 */
	public void updateLabel(Label label) throws ModelException {
		checkLabelExistsInLanguage(label);

		deleteLabelForLanguagePostCheck(label);
		addLabelPostCheck(label);
	}
	
	/**
	 * Delete the supplied label from this concept scheme.
	 * If the label is not present on the concept scheme then an exception will be thrown.
	 * @param label
	 * @throws ModelException
	 */
	public void deleteLabel(Label label) throws ModelException {
		checkLabelExists(label);
		
		deleteLabelPostCheck(label);
	}
	
	private void deleteLabelForLanguagePostCheck(Label label) {
		String sparql = "DELETE { ?conceptSchemeURI rdfs:label ?labelValue } WHERE { ?conceptSchemeURI rdfs:label ?labelValue . FILTER(STRLANG(?label) = STR(?labelLanguage)) }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("conceptSchemeURI", resource);
		parameterizedSparql.setParam("labelLanguage", model.createLiteral(label.getValue(), label.getLanguageCode()));
		
		UpdateRequest updateRequest = UpdateFactory.create(parameterizedSparql.getCommandText()); 
		UpdateAction.execute(updateRequest, model);
	}
	
	private void deleteLabelPostCheck(Label label) {
		String sparql = "DELETE { ?conceptSchemeURI rdfs:label ?labelValue } WHERE { }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("conceptSchemeURI", resource);
		parameterizedSparql.setParam("labelLanguage", model.createLiteral(label.getValue(), label.getLanguageCode()));
		
		UpdateRequest updateRequest = UpdateFactory.create(parameterizedSparql.getCommandText()); 
		UpdateAction.execute(updateRequest, model);
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
		String sparql = "ASK WHERE { ?conceptSchemeURI rdfs:label ?label . FILTER(LANG(?label) = STR(?labelLanguage)) }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("conceptSchemeURI", resource);
		parameterizedSparql.setParam("labelLanguage", model.createLiteral(label.getLanguageCode(), ""));
		
		Query query = QueryFactory.create(parameterizedSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		return qexec.execAsk();
	}

	private void checkLabelExists(Label label) throws ModelException {
		if (!labelExists(label)) throw new ModelException("Attempting to delete label for '%s'. This already has no label in language '%s'", resource.getURI(), label.getLanguageCode());
	}

	@SuppressWarnings("unused")
	private void checkLabelDoesntExist(Label label) throws ModelException {
		if (labelExists(label)) throw new ModelException("Attempting to create label for '%s'. This already has a label in language '%s'", resource.getURI(), label.getLanguageCode());
	}

	private boolean labelExists(Label label) {
		String sparql = "ASK * WHERE { ?conceptSchemeURI rdfs:label ?label }";
		ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
		parameterizedSparql.setCommandText(sparql);
		parameterizedSparql.setParam("conceptSchemeURI", resource);
		parameterizedSparql.setParam("selectedLabel", model.createLiteral(label.getValue(), label.getLanguageCode()));
		
		Query query = QueryFactory.create(parameterizedSparql.asQuery());

		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		return qexec.execAsk();
	}
	

}

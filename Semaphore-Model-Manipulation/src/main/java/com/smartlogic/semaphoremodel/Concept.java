package com.smartlogic.semaphoremodel;

import static org.apache.jena.ext.com.google.common.base.Preconditions.checkNotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Sets;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.SKOSXL;

import com.smartlogic.semaphoremodel.OrderedCollection.Type;

public class Concept extends IdentifiableObject {

  private final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  protected Concept(Model model, Resource resource) {
    super(model, resource);
  }

  public void addMetadata(StringMetadataType metadataType, String value, Language language) {
    if (null == language) {
      resource.addProperty(metadataType.getProperty(), model.createLiteral(value));
    } else {
      resource.addProperty(metadataType.getProperty(),
          model.createLiteral(value, language.getCode()));
    }
  }

  /**
   * Add date metadata value using Calendar instance
   *
   * @param metadataType
   *          the property type
   * @param calendar
   *          the value
   */
  public void addMetadata(CalendarMetadataType metadataType, Calendar calendar) {
    resource.addProperty(metadataType.getProperty(),
        model.createTypedLiteral(getFormattedDate(calendar), XSDDatatype.XSDdate));
  }

  /**
   * Add date metadata value
   *
   * @param metadataType
   *          the property type
   * @param date
   *          the value
   */
  public void addMetadata(CalendarMetadataType metadataType, LocalDate date) {
    resource.addProperty(metadataType.getProperty(),
        model.createTypedLiteral(date.format(DateTimeFormatter.ISO_DATE), XSDDatatype.XSDdate));
  }

  /**
   * Add boolean metadata property.
   *
   * @param metadataType
   *          the metadata type
   * @param bool
   *          the value
   */
  public void addMetadata(BooleanMetadataType metadataType, boolean bool) {
    resource.addProperty(metadataType.getProperty(), model.createTypedLiteral(bool));
  }

  /**
   * Add integer valued metadata property
   *
   * @param metadataType
   *          the metadata type
   * @param value
   *          the value
   */
  public void addMetadata(IntegerMetadataType metadataType, int value) {
    resource.addProperty(metadataType.getProperty(), model.createTypedLiteral(value));
  }

  /**
   * Get the set of values for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @return the set of values
   */
  public Set<Integer> getMetadata(IntegerMetadataType metadataType) {
    return resource.listProperties(metadataType.getProperty()).toList().stream()
        .map(Statement::getInt).collect(Collectors.toSet());
  }

  /**
   * Get the set of values for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @return the set of values
   */
  public Set<String> getMetadata(StringMetadataType metadataType, Language languageCode) {
    String langCode = languageCode == null ? "" : languageCode.getCode();
    return resource.listProperties(metadataType.getProperty()).toList().stream()
        .filter(e -> e.getLiteral().getLanguage().equals(langCode)).map(Statement::getString)
        .collect(Collectors.toSet());
  }

  /**
   * Get the set of values for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @return the set of values
   */
  public Set<LocalDate> getMetadata(CalendarMetadataType metadataType) {
    return resource.listProperties(metadataType.getProperty()).toList().stream()
        .map(e -> LocalDate.parse(e.getString())).collect(Collectors.toSet());
  }

  /**
   * Get the set of values for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @return the set of values
   */
  public Set<Boolean> getMetadata(BooleanMetadataType metadataType) {
    return resource.listProperties(metadataType.getProperty()).toList().stream()
        .map(Statement::getBoolean).collect(Collectors.toSet());
  }

  /**
   * Remove all values of the metadata type
   *
   * @param metadataType
   *          the metadata type
   */
  public void removeAllMetadata(MetadataType metadataType) {
    resource.removeAll(metadataType.getProperty());
  }

  /**
   * Remove the specified value for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @param value
   *          the value
   */
  public void removeMetadata(IntegerMetadataType metadataType, Integer value) {
    List<Statement> toDelete = resource.listProperties(metadataType.getProperty()).toList().stream()
        .filter(e -> e.getInt() == value).collect(Collectors.toList());
    model.remove(toDelete);
  }

  /**
   * Remove the specified language-qualified literal for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @param value
   *          the value
   * @param languageCode
   *          the language code. Null for language-independent literal
   */
  public void removeMetadata(StringMetadataType metadataType, String value, Language languageCode) {
    String langCode = languageCode == null ? "" : languageCode.getCode();
    List<Statement> toDelete = resource.listProperties(metadataType.getProperty()).toList().stream()
        .filter(e -> e.getLiteral().getLanguage().equals(langCode))
        .filter(e -> e.getLiteral().getString().equals(value)).collect(Collectors.toList());
    model.remove(toDelete);
  }

  /**
   * Remove the boolean metadata type properties
   *
   * @param metadataType
   *          the metadata type
   */
  public void removeMetadata(BooleanMetadataType metadataType) {
    resource.removeAll(metadataType.getProperty());
  }

  /**
   * Remove the value for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @param value
   *          the value
   */
  public void removeMetadata(CalendarMetadataType metadataType, LocalDate value) {
    List<Statement> toDelete = resource.listProperties(metadataType.getProperty()).toList().stream()
        .filter(e -> LocalDate.parse(e.getString()).equals(value)).collect(Collectors.toList());
    model.remove(toDelete);
  }

  /**
   * Remove the value for the metadata type
   *
   * @param metadataType
   *          the metadata type
   * @param value
   *          the value
   */
  public void removeMetadata(DateMetadataType metadataType, LocalDate value) {
    List<Statement> toDelete = resource.listProperties(metadataType.getProperty()).toList().stream()
        .filter(e -> LocalDate.parse(e.getString()).equals(value)).collect(Collectors.toList());
    model.remove(toDelete);
  }

  /**
   * Add a property statement from this concept to the specified concept of the specified type.
   *
   * @param relationshipType
   *          the type of relationship property
   * @param concept
   *          the concept to which to use as the object of the property statements
   */
  public void addRelation(RelationshipType relationshipType, Concept concept) {
    resource.addProperty(relationshipType.getProperty(), concept.getResource());
  }

  /**
   * Add a property statement from this concept to the specified concept of the specified type and
   * also add the inverse property.
   *
   * @param relationshipType
   *          the relationship type
   * @param concept
   *          the concept
   */
  public void addRelationWithInverse(RelationshipType relationshipType, Concept concept) {
    addRelation(relationshipType, concept);
    Resource resource = model.createResource(concept.getURI().toString());
    resource.addProperty(relationshipType.getInverseProperty(), this.getResource());

  }

  /**
   * Add a statement to this concept specifying its class.
   *
   * @param conceptClass
   *          the concept class.
   */
  public void addConceptClass(ConceptClass conceptClass) {
    resource.addProperty(RDF.type, conceptClass.getResource());
  }

  /**
   * Relates this concept to the specified concept via skos:related.
   *
   * @param concept
   *          the concept
   */
  public void addRelated(Concept concept) {
    checkNotNull(concept);
    resource.addProperty(SKOS.related, concept.getResource());
    concept.getResource().addProperty(SKOS.related, resource);
  }

  /**
   * Adds the specified concept as a parent (skos:broader) of this concept.
   *
   * @param concept
   *          the concept
   */
  public void addBroader(Concept concept) {
    checkNotNull(concept);
    resource.addProperty(SKOS.broader, concept.getResource());
  }

  /**
   * Adds the specified concept as a child (skos:narrower) to this concept.
   *
   * @param concept
   *          the concept
   */
  public void addNarrower(Concept concept) {
    checkNotNull(concept);
    concept.getResource().addProperty(SKOS.broader, resource);
  }

  /**
   * Adds a relationship from this concept to the other.
   *
   * @param relationshipType
   *          the relationship type
   * @param concept2
   *          the concept
   */
  public void addRelationship(ConceptToConceptRelationshipType relationshipType, Concept concept2) {
    checkNotNull(relationshipType);
    checkNotNull(concept2);
    resource.addProperty(relationshipType.getProperty(), concept2.getResource());
    if (relationshipType.getInverseProperty() == null) {
      concept2.getResource().addProperty(relationshipType.getProperty(), this.getResource()); // Symmetric
      // property
    } else {
      concept2.getResource().addProperty(relationshipType.getInverseProperty(), this.getResource()); // Asymmetric
      // property
    }
  }

  /**
   * Adds the preferred label - if there is already a preferred label of this language an exception
   * is thrown.
   *
   * @param label
   *          - the preferred label in the requested language.
   * @throws ModelException
   *           - thrown if there is already a label of this language for the object
   */
  public void addPrefLabel(Label label) throws ModelException {
    checkNotNull(label);
    checkPrefLabelDoesntExistInLanguage(label);
    addPrefLabelPostCheck(label);
  }

  /**
   * Set the preferred label. After this operation, the Concept Scheme will have this label and no
   * other in this language. It does not matter whether there already was a label of this language
   * present.
   *
   * @param label
   *          - the label to be set on this object.
   */
  public void setPrefLabel(Label label) {
    checkNotNull(label);
    deletePrefLabelForLanguagePostCheck(label);
    addPrefLabelPostCheck(label);
  }

  /**
   * updateLabel Delete the current label in this language and replace it with the supplied one If
   * there was no label in this language, then throw an exception
   *
   * @param label
   *          - the label to be updated on this object.
   * @throws ModelException
   *           - thrown if there is no label in the supplied language to update
   */
  public void updatePrefLabel(Label label) throws ModelException {
    checkNotNull(label);
    checkLabelExistsInLanguage(label);
    deletePrefLabelForLanguagePostCheck(label);
    addPrefLabelPostCheck(label);
  }

  /**
   * Delete the supplied preferred label from this concept scheme. If the label is not present on
   * the concept scheme then an exception will be thrown.
   *
   * @param label
   *          - the label to be deleted from this object.
   * @throws ModelException
   *           - thrown if the label doesn't exist on this object
   */
  public void deletePrefLabel(Label label) throws ModelException {
    checkNotNull(label);
    checkPrefLabelExists(label);
    deletePrefLabelForLanguagePostCheck(label);
  }

  public Label getPrefLabelForLanguage(Language language) throws ModelException {
    return getPrefLabelForLanguage(language.getCode());
  }

  public Label getPrefLabelForLanguage(String langCode) throws ModelException {
    String sparql = "PREFIX skosxl: <http://www.w3.org/2008/05/skos-xl#>\n" +
        "select ?labelUri ?labelValue\n" +
        "where {\n" +
        "  ?conceptUri skosxl:prefLabel ?labelUri .\n" +
        "  ?labelUri skosxl:literalForm ?labelValue .\n" +
        "  filter(lang(?labelValue) = ?labelLanguage)  \n" +
        "}";
    ParameterizedSparqlString pSparql = new ParameterizedSparqlString();
    pSparql.setCommandText(sparql);
    pSparql.setIri("conceptUri", resource.getURI());
    pSparql.setLiteral("labelLanguage",
        Strings.isNullOrEmpty(langCode) ? model.createLiteral("") : model.createLiteral(langCode));
    QueryExecution qe = QueryExecutionFactory.create(pSparql.asQuery(), model);
    ResultSet rs = qe.execSelect();
    Label label = null;
    if (rs.hasNext()) {
      QuerySolution qs = rs.next();
      label = new Label(qs.getResource("labelUri"), qs.getLiteral("labelValue"));
    }
    return label;
  }

  /**
   * Return Concepts that are related to this concept by the specified relationship type property.
   *
   * @param relationship
   *          the relationship property
   * @return the collection of related concepts
   */
  public Collection<Concept> getRelated(Property relationship) {
    checkNotNull(relationship);
    Collection<Concept> returnData = new HashSet<>();
    StmtIterator stmtIterator = resource.listProperties(relationship);

    while (stmtIterator.hasNext()) {
      Statement statement = stmtIterator.next();
      Resource relatedResource = statement.getObject().asResource();
      returnData.add(new Concept(model, relatedResource));
    }
    return returnData;
  }

  /**
   * Return the Concepts that are related to this concept by the specified relationship type.
   *
   * @param relationshipType
   *          the relationship type
   * @return the collection of concepts.
   */
  public Collection<Concept> getRelated(RelationshipType relationshipType) {
    checkNotNull(relationshipType);
    return getRelated(relationshipType.getProperty());
  }

  /**
   * Add the specified alt label to this concept using the skosxl:altLabel default property.
   *
   * @param altLabel
   *          the alternative label
   */
  public void addAltLabel(Label altLabel) {
    checkNotNull(altLabel);
    addAltLabelPostCheck(altLabel);
  }

  /**
   * Add the specified alt label to this concept using the specified alt label property.
   *
   * @param relationshipType
   *          the relationship type
   * @param altLabel
   *          the alternative label
   */
  public void addAltLabel(RelationshipType relationshipType, Label altLabel) {
    checkNotNull(altLabel);
    checkNotNull(relationshipType);
    addAltLabelPostCheck(relationshipType, altLabel);

  }

  /**
   * Return all alt labels defined by skosxl:altLabel.
   *
   * @return a set of Labels
   * @throws ModelException
   *           a model exception occurred.
   */
  public Set<Label> getAltLabels() throws ModelException {
    Set<Label> altLabels = Sets.newHashSet();
    ParameterizedSparqlString pSparql = new ParameterizedSparqlString();
    pSparql.setCommandText("PREFIX skosxl: <http://www.w3.org/2008/05/skos-xl#>\n" +
        "select ?labelUri ?labelValue\n" +
        "where {\n" +
        "  ?conceptUri skosxl:altLabel ?labelUri .\n" +
        "  ?labelUri skosxl:literalForm ?labelValue .\n" +
        "}\n");
    pSparql.setIri("conceptUri", resource.getURI());
    QueryExecution qe = QueryExecutionFactory.create(pSparql.asQuery(), model);
    ResultSet rs = qe.execSelect();
    while (rs.hasNext()) {
      QuerySolution qs = rs.next();
      Resource labelRes = qs.getResource("labelUri");
      Literal labelLit = qs.getLiteral("labelValue");
      Label l = new Label(labelRes, labelLit);
      altLabels.add(l);
    }
    return altLabels;
  }

  /**
   * Delete the specified alt label value for the default skosxl:altLabel property
   *
   * @param label
   *          the alternative label
   * @throws ModelException
   *           a model exception occurred.
   */
  public void deleteAltLabel(Label label) throws ModelException {
    checkNotNull(label);
    if (!checkAltLabelExists(label)) {
      throw new ModelException("Label " + label.getValue() + " does not currently exist.");
    }

    deleteLabelPostCheck(label, SKOSXL.altLabel);
  }

  private String getFormattedDate(Date date) {
    return getFormattedDate(date.toInstant());
  }

  private String getFormattedDate(Calendar calendar) {
    return getFormattedDate(calendar.toInstant());
  }

  private String getFormattedDate(Instant instant) {
    return dateFormatter.format(instant.atZone(ZoneId.systemDefault()).toLocalDate());
  }

  private void deleteLabelPostCheck(Label label, Property altLabelProp) {
    checkNotNull(label);
    checkNotNull(altLabelProp);
    String sparql = "PREFIX skosxl: <http://www.w3.org/2008/05/skos-xl#>\n" +
        "delete {\n" +
        "  ?conceptUri ?labelProp ?pl .\n" +
        "  ?pl ?p ?o .\n" +
        "} where {\n" +
        "  ?conceptUri ?labelProp ?pl .\n" +
        "  ?pl skosxl:literalForm ?labelLit .\n" +
        "  ?pl ?p ?o .\n" +
        "}";
    ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
    parameterizedSparql.setCommandText(sparql);
    parameterizedSparql.setParam("conceptUri", resource);
    parameterizedSparql.setParam("labelProp", altLabelProp);
    Literal literal;
    if (Strings.isNullOrEmpty(label.getLanguageCode())) {
      literal = model.createLiteral(label.getValue());
    } else {
      literal = model.createLiteral(label.getValue(), label.getLanguageCode());
    }
    parameterizedSparql.setParam("labelLit", literal);
    System.out.println(parameterizedSparql.getCommandText());

    UpdateAction.execute(parameterizedSparql.asUpdate(), model);
  }

  private void deletePrefLabelForLanguagePostCheck(Label label) {
    deleteLabelPostCheck(label, SKOSXL.prefLabel);
    String sparql = "PREFIX skosxl: <http://www.w3.org/2008/05/skos-xl#>\n" +
        "delete {\n" +
        "  ?conceptUri skosxl:prefLabel ?pl .\n" +
        "  ?pl skosxl:literalForm ?label .\n" +
        "  ?pl ?p ?o .\n" +
        "} where {\n" +
        "  ?conceptUri skosxl:prefLabel ?pl .\n" +
        "  ?pl skosxl:literalForm ?label .\n" +
        "  ?pl ?p ?o .\n" +
        "  filter(lang(?label) = ?labelLanguage)  \n" +
        "}";
    ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
    parameterizedSparql.setCommandText(sparql);
    parameterizedSparql.setParam("conceptUri", resource);
    parameterizedSparql.setParam("labelLanguage", model.createLiteral(label.getLanguageCode()));

    UpdateAction.execute(parameterizedSparql.asUpdate(), model);
  }

  private void addPrefLabelPostCheck(Label label) {
    String labelUri = resource.getURI() +
        Utils.encodeStringForURI(label.getValue()) +
        (Strings.isNullOrEmpty(label.getLanguageCode()) ? "" : "_" + label.getLanguageCode());
    Resource prefLabelRes = model.createResource(labelUri);
    prefLabelRes.addProperty(RDF.type, SKOSXL.Label);
    Literal literal;
    if (Strings.isNullOrEmpty(label.getLanguageCode())) {
      literal = model.createLiteral(label.getValue());
    } else {
      literal = model.createLiteral(label.getValue(), label.getLanguageCode());
    }
    prefLabelRes.addProperty(SKOSXL.literalForm, literal);
    resource.addProperty(SKOSXL.prefLabel, prefLabelRes);
  }

  private void addAltLabelPostCheck(Label altLabel) {
    addAltLabelPostCheck(SKOSXL.altLabel, altLabel);
  }

  private void addAltLabelPostCheck(RelationshipType relationshipType, Label altLabel) {
    addAltLabelPostCheck(relationshipType.getProperty(), altLabel);
  }

  private void addAltLabelPostCheck(Property relationshipTypeProperty, Label altLabel) {
    Resource altLabelRes = model.createResource(resource.getURI() +
        Utils.encodeStringForURI(altLabel.getValue()) +
        (Strings.isNullOrEmpty(altLabel.getLanguageCode()) ? ""
            : "_" + altLabel.getLanguageCode()));
    resource.addProperty(relationshipTypeProperty, altLabelRes);
    altLabelRes.addProperty(RDF.type, SKOSXL.Label);
    Literal literal;
    if (Strings.isNullOrEmpty(altLabel.getLanguageCode())) {
      literal = model.createLiteral(altLabel.getValue());
    } else {
      literal = model.createLiteral(altLabel.getValue(), altLabel.getLanguageCode());
    }
    altLabelRes.addProperty(SKOSXL.literalForm, literal);
    altLabel.setResource(altLabelRes);
  }

  private void checkLabelExistsInLanguage(Label label) throws ModelException {
    if (!prefLabelExistsInLanguage(label)) {
      throw new ModelException(
          "Attempting to delete label for '%s'. This already has no label in language '%s'",
          resource.getURI(), label.getLanguageCode());
    }
  }

  private void checkPrefLabelDoesntExistInLanguage(Label label) throws ModelException {
    if (prefLabelExistsInLanguage(label)) {
      throw new ModelException(
          "Attempting to create label for '%s'. This already has a label in language '%s'",
          resource.getURI(), label.getLanguageCode());
    }
  }

  private boolean checkAltLabelExists(Label label) {
    String sparql = "ASK WHERE { ?conceptUri skosxl:altLabel/skosxl:literalForm ?label . " +
        "FILTER(LANG(?label) = STR(?labelLanguage)) }";
    ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
    parameterizedSparql.setCommandText(sparql);
    parameterizedSparql.setParam("conceptUri", resource);
    parameterizedSparql.setParam("labelLanguage",
        Strings.isNullOrEmpty(label.getLanguageCode()) ? model.createLiteral("")
            : model.createLiteral(label.getLanguageCode()));

    Query query = QueryFactory.create(parameterizedSparql.asQuery());

    try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
      return qexec.execAsk();
    }
  }

  private boolean prefLabelExistsInLanguage(Label label) {
    String sparql = "ASK WHERE { ?conceptUri skosxl:prefLabel/skosxl:literalForm ?label . " +
        "FILTER(LANG(?label) = STR(?labelLanguage)) }";
    ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
    parameterizedSparql.setCommandText(sparql);
    parameterizedSparql.setParam("conceptUri", resource);
    parameterizedSparql.setParam("labelLanguage",
        Strings.isNullOrEmpty(label.getLanguageCode()) ? model.createLiteral("")
            : model.createLiteral(label.getLanguageCode()));

    Query query = QueryFactory.create(parameterizedSparql.asQuery());

    try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
      return qexec.execAsk();
    }
  }

  private void checkPrefLabelExists(Label label) throws ModelException {
    if (!prefLabelExists(label)) {
      throw new ModelException(
          "Attempting to delete label for '%s'. This already has no label in language '%s'",
          resource.getURI(), label.getLanguageCode());
    }
  }

  @SuppressWarnings("unused")
  private void checkPrefLabelDoesntExist(Label label) throws ModelException {
    if (prefLabelExists(label)) {
      throw new ModelException(
          "Attempting to create label for '%s'. This already has a label in language '%s'",
          resource.getURI(), label.getLanguageCode());
    }
  }

  private boolean prefLabelExists(Label label) {
    String sparql = "ASK * WHERE { ?conceptUri skosxl:prefLabel/skosxl:literalForm ?label }";
    ParameterizedSparqlString parameterizedSparql = new ParameterizedSparqlString(model);
    parameterizedSparql.setCommandText(sparql);
    parameterizedSparql.setParam("conceptUri", resource);
    parameterizedSparql.setParam("label",
        model.createLiteral(label.getValue(), label.getLanguageCode()));

    Query query = QueryFactory.create(parameterizedSparql.asQuery());

    try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
      return qexec.execAsk();
    }
  }

  private OrderedCollection addOrderedCollection(OrderedCollection.Type orderedCollectionType,
      Property firstProperty, Resource firstValue) {

    Resource orderedCollectionResource = model.createResource(
        this.getResource().toString() + "_OrderedCollection_" + UUID.randomUUID().toString());
    OrderedCollection orderedCollection = new OrderedCollection(model, orderedCollectionResource,
        orderedCollectionType, this, firstProperty, firstValue);
    resource.addProperty(SEMORD.hasOrderedCollection, orderedCollectionResource);

    return orderedCollection;
  }

  public OrderedCollection addOrderedCollection(Type type,
      ConceptToConceptRelationshipType relationshipType, Concept concept) {
    this.addRelationWithInverse(relationshipType, concept);
    return addOrderedCollection(type, relationshipType.getProperty(), concept.getResource());
  }

}

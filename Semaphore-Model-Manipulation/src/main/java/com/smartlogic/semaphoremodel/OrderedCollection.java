package com.smartlogic.semaphoremodel;

import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class OrderedCollection extends IdentifiableObject {

  private final Concept concept;

  private PropertyValueMember lastPropertyValueMember;

  protected OrderedCollection(Model model, Resource resource, Type orderedCollectionType,
      Concept concept, Property firstProperty, Resource firstValue) {
    super(model, resource);
    this.concept = concept;

    resource.addProperty(SEMORD.orderedCollectionOf, concept.getResource());
    resource.addProperty(RDF.type, SEMORD.OrderedCollection);
    resource.addProperty(SEMORD.propertyValueGroup, orderedCollectionType.getResource());

    Resource propertyValueResource = model.createResource(
        concept.getResource().toString() + "_MemberList_" + UUID.randomUUID().toString());

    lastPropertyValueMember =
        new PropertyValueMember(model, propertyValueResource, firstProperty, firstValue);
    resource.addProperty(SEMORD.memberList, propertyValueResource);

  }

  private void addValue(Property property, Resource value) {
    Resource propertyValueResource = model.createResource(
        concept.getResource().toString() + "_MemberList_" + UUID.randomUUID().toString());

    PropertyValueMember nextPropertyValueMember =
        new PropertyValueMember(model, propertyValueResource, property, value);
    lastPropertyValueMember.addNext(nextPropertyValueMember);

    lastPropertyValueMember = nextPropertyValueMember;
  }

  public enum Type {
    NARROWER(SEMORD.NarrowerConcepts);

    private final Resource resource;

    Type(Resource resource) {
      this.resource = resource;
    }

    public Resource getResource() {
      return resource;
    }

  }

  public void addValue(ConceptToConceptRelationshipType relationshipType, Concept concept) {
    this.concept.addRelationWithInverse(relationshipType, concept);
    addValue(relationshipType.getProperty(), concept.getResource());

  }

}

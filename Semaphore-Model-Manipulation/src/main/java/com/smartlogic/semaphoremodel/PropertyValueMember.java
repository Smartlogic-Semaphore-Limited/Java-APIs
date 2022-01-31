package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

public class PropertyValueMember extends IdentifiableObject {

  public PropertyValueMember(Model model, Resource resource, Property firstProperty,
      Resource firstValue) {
    super(model, resource);
    resource.addProperty(RDF.type, SEMORD.PropertyValueMember);
    resource.addProperty(SEMORD.property, firstProperty);
    resource.addProperty(SEMORD.value, firstValue);
  }

  public void addNext(PropertyValueMember nextPropertyValueMember) {
    resource.addProperty(SEMORD.next, nextPropertyValueMember.getResource());
  }

}

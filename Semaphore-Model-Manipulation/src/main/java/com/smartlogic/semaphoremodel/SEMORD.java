package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class SEMORD {

  protected static Resource PropertyValueMember = ResourceFactory
      .createResource("http://www.smartlogic.com/2018/03/semaphore-ordering#PropertyValueMember");

  protected static Resource OrderedCollection = ResourceFactory
      .createResource("http://www.smartlogic.com/2018/03/semaphore-ordering#OrderedCollection");

  protected static Resource NarrowerConcepts = ResourceFactory
      .createResource("http://www.smartlogic.com/2014/08/semaphore-core#NarrowerConcepts");

  protected static Property property = ResourceFactory
      .createProperty("http://www.smartlogic.com/2018/03/semaphore-ordering#property");

  protected static Property value =
      ResourceFactory.createProperty("http://www.smartlogic.com/2018/03/semaphore-ordering#value");

  protected static Property next =
      ResourceFactory.createProperty("http://www.smartlogic.com/2018/03/semaphore-ordering#next");

  protected static Property orderedCollectionOf = ResourceFactory
      .createProperty("http://www.smartlogic.com/2018/03/semaphore-ordering#orderedCollectionOf");

  protected static Property propertyValueGroup = ResourceFactory
      .createProperty("http://www.smartlogic.com/2014/08/semaphore-core#propertyValueGroup");

  protected static Property memberList = ResourceFactory
      .createProperty("http://www.smartlogic.com/2018/03/semaphore-ordering#memberList");

  protected static Property hasOrderedCollection = ResourceFactory
      .createProperty("http://www.smartlogic.com/2018/03/semaphore-ordering#hasOrderedCollection");

}

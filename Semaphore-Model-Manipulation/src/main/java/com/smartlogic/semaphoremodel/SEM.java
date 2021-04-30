package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class SEM {

  protected static Property guid =
      ResourceFactory.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
  protected static Property defaultProperty = ResourceFactory
      .createProperty("http://www.smartlogic.com/2014/08/semaphore-core#DefaultProperty");

  protected static Property alwaysVisibleProperty = ResourceFactory
      .createProperty("http://www.smartlogic.com/2014/08/semaphore-core#AlwaysVisibleProperty");

  protected static Property characterEscaping = ResourceFactory
      .createProperty("http://www.smartlogic.com/2014/08/semaphore-core#characterEscaping");

  protected static Resource EscapeSpecialCharacters = ResourceFactory
      .createResource("http://www.smartlogic.com/2014/08/semaphore-core#EscapeSpecialCharacters");
  protected static Resource NoEscaping =
      ResourceFactory.createResource("http://www.smartlogic.com/2014/08/semaphore-core#NoEscaping");

  protected static Property caseSensitivity = ResourceFactory
      .createProperty("http://www.smartlogic.com/2014/08/semaphore-core#caseSensitivity");
  protected static Resource CaseSensitive = ResourceFactory
      .createResource("http://www.smartlogic.com/2014/08/semaphore-core#CaseSensitive");
  protected static Resource CaseInsensitive = ResourceFactory
      .createResource("http://www.smartlogic.com/2014/08/semaphore-core#CaseInsensitive");

  protected static Property stemming =
      ResourceFactory.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#stemming");
  protected static Resource StemmingOn =
      ResourceFactory.createResource("http://www.smartlogic.com/2014/08/semaphore-core#StemmingOn");
  protected static Resource StemmingOff = ResourceFactory
      .createResource("http://www.smartlogic.com/2014/08/semaphore-core#StemmingOff");

}

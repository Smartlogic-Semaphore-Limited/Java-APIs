package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class SEM  {

	protected static Property guid = ResourceFactory.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#guid");
	protected static Property defaultProperty = ResourceFactory.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#DefaultProperty");
	
	protected static Property alwaysVisibleProperty = ResourceFactory.createProperty("http://www.smartlogic.com/2014/08/semaphore-core#AlwaysVisibleProperty");
}

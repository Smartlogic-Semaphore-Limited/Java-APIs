package com.smartlogic.semaphoremodel;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.testng.annotations.Test;

public class TestMetadataType {
  @Test
  public void testMetadataType() throws ModelException, URISyntaxException {
	  URI uri = new URI("http://smartlogic.com/metadata#test");
	  Label label = new Label("Metadata label", Language.getLanguage("en"));
	  
	  SemaphoreModel semaphoreModel = new SemaphoreModel();
	  StringMetadataType stringMetadataType = semaphoreModel.createStringMetadataType(uri, label);
	  
	  MetadataType stringMetadataType1 = semaphoreModel.getMetadataType(uri);
	  
	  assertEquals(stringMetadataType, stringMetadataType1);
	  
	  
  }
}

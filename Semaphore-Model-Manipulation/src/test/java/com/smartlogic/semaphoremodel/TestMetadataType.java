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
	  stringMetadataType.setAlwaysVisibleProperty();
	  
	  MetadataType stringMetadataType1 = semaphoreModel.getMetadataType(uri);
	  stringMetadataType1.setAlwaysVisibleProperty();
	  stringMetadataType1.removeAlwaysVisibleProperty();
	  assertEquals(stringMetadataType, stringMetadataType1);
	  
	  
  }
}

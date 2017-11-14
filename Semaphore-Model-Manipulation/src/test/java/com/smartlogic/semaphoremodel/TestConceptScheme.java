package com.smartlogic.semaphoremodel;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.smartlogic.semaphoremodel.ConceptScheme;
import com.smartlogic.semaphoremodel.Label;
import com.smartlogic.semaphoremodel.Language;
import com.smartlogic.semaphoremodel.ModelException;
import com.smartlogic.semaphoremodel.SemaphoreModel;

public class TestConceptScheme {

	private SemaphoreModel semaphoreModel;
	private Language english = Language.getLanguage("en");
	private Language italian = Language.getLanguage("it");
	
	@BeforeTest
	public void loadModel() {
		semaphoreModel = new SemaphoreModel(new File("src/test/resources/TestModels/ConceptSchemeTest.ttl"));
	}
	
	@AfterTest
	public void dumpModel() throws FileNotFoundException {
		semaphoreModel.write(new File("ConceptModelDump.ttl"));
	}
	
	@Test
	public void testExistingConceptScheme() throws URISyntaxException, ModelException {
		ConceptScheme conceptSchemeExists = semaphoreModel.getConceptScheme(new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyFirstConceptScheme"));
		assertNotNull(conceptSchemeExists, "Concept Scheme exists");
		
		Label labelEn = conceptSchemeExists.getLabel(english); 
		assertEquals(labelEn.getValue(), "My first concept scheme", "Label Value");

		Label labelIt = conceptSchemeExists.getLabel(italian); 
		assertNull(labelIt,  "No Italian version available");
	}
	
	@Test
	public void testNonExistingConceptScheme() throws URISyntaxException {
		ConceptScheme conceptSchemeNotExists = semaphoreModel.getConceptScheme(new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MySecondConceptScheme"));
		assertNull(conceptSchemeNotExists, "Concept Scheme doesn't exist");
	}
	
	@Test
	public void testAddingIllegalConceptScheme() throws URISyntaxException {
		try {
			semaphoreModel.createConceptScheme(new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyFirstConceptScheme"), new Label("hello", english), null);
			fail("We shouldn't get here - creating a duplicate Concept Scheme");
		} catch (ModelException e) {
			assertEquals(e.getMessage(), "Attempting to create concept scheme with URI - 'http://example.com/ConceptSchemeTest#ConceptScheme/MyFirstConceptScheme'. This URI is already in use.");
		}
	}
	
	@Test
	public void testAddingIllegalLabel() throws URISyntaxException {
		ConceptScheme conceptSchemeExists = semaphoreModel.getConceptScheme(new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyFirstConceptScheme"));
		try {
			conceptSchemeExists.addLabel(new Label("Illegal label", english));
			fail("We shouldn't get here - createding a duplicate label");
		} catch (ModelException e) {
			assertEquals(e.getMessage(), "Attempting to create label for 'http://example.com/ConceptSchemeTest#ConceptScheme/MyFirstConceptScheme'. This already has a label in language 'en'");
		}
	}

	@Test
	public void testAddingConceptScheme() throws URISyntaxException, ModelException {
		semaphoreModel.createConceptScheme(new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyCreatedConceptScheme"), new Label("hello", english), null);

		ConceptScheme createdConceptScheme = semaphoreModel.getConceptScheme(new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyCreatedConceptScheme"));
		assertNotNull(createdConceptScheme, "Concept Scheme has been created");
		
		Label labelEn = createdConceptScheme.getLabel(english); 
		assertEquals(labelEn.getValue(), "hello", "Created label Value");
		
		createdConceptScheme.updateLabel(new Label("goodbye", english));
		Label updatedLabelEn = createdConceptScheme.getLabel(english); 
		assertEquals(updatedLabelEn.getValue(), "goodbye", "Updated label Value (en)");

		createdConceptScheme.setLabel(new Label("ciao", italian));
		Label setLabelIt = createdConceptScheme.getLabel(italian); 
		assertEquals(setLabelIt.getValue(), "ciao", "Set label Value (it)");

		createdConceptScheme.setLabel(new Label("adios", italian));
		Label setLabelIt2 = createdConceptScheme.getLabel(italian); 
		assertEquals(setLabelIt2.getValue(), "adios", "Set(2) label Value (it)");

		
		try {
			createdConceptScheme.deleteLabel(new Label("swizzle", english));
			fail("Delete label should have thrown an exception");
		} catch (ModelException e) {
			assertEquals(e.getMessage(), "Attempting to delete label \"swizzle\"en for 'http://example.com/ConceptSchemeTest#ConceptScheme/MyCreatedConceptScheme'. This label does not exist");
		}

		createdConceptScheme.deleteLabel(new Label("goodbye", english));
		Label deletedLabelEn = createdConceptScheme.getLabel(english); 
		assertNull(deletedLabelEn, "Deleted label Value (en)");

		
		// We are "removing" a label that doesn't exist. This will do nothing
		createdConceptScheme.removeLabel(new Label("goodbye", italian));
		Label removedLabelIt = createdConceptScheme.getLabel(italian); 
		assertEquals(removedLabelIt.getValue(), "adios", "Remove(1) label Value (it)");
		
		// We are "removing" a label that exists. 
		createdConceptScheme.removeLabel(new Label("adios", italian));
		Label removedLabelIt2 = createdConceptScheme.getLabel(italian); 
		assertNull(removedLabelIt2, "Remove(2) label Value (it)");
		
	}

}

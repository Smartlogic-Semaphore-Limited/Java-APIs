package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.SKOS;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;

import static org.testng.Assert.*;

public class TestConceptScheme extends AbstractTest {

	private SemaphoreModel semaphoreModel;
	private Language english = Language.getLanguage("en");
	private Language italian = Language.getLanguage("it");
	
	@BeforeTest
	public void loadModel() {
		semaphoreModel = new SemaphoreModel(new File("src/test/resources/TestModels/ConceptSchemeTest.ttl"));
	}
	
	@AfterTest
	public void dumpModel() throws FileNotFoundException {
		dumpModel(semaphoreModel);
	}
	
	@Test
	public void testExistingConceptScheme() throws URISyntaxException, ModelException {
		ConceptScheme conceptSchemeExists = semaphoreModel.getConceptScheme(new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyFirstConceptScheme"));
		assertNotNull(conceptSchemeExists, "Concept Scheme exists");

		Label labelEn = conceptSchemeExists.getLabel(english);
		assertEquals(labelEn.getValue(), "My first concept scheme", "Label Value");

		Label labelIt = conceptSchemeExists.getLabel(italian);
		assertNull(labelIt, "No Italian version available");

		Set<Concept> topConcepts = conceptSchemeExists.getTopConcepts();
		assertEquals(topConcepts.size(), 0, "Size of top concepts start");

		Concept tc1 = semaphoreModel.createConcept(URI.create("urn:foo:bar:tc1"), new Label("TC1", english));
		conceptSchemeExists.addTopConcept(tc1);
		assertEquals(conceptSchemeExists.getTopConcepts().size(), 1, "Size of top concepts after adding 1");

		Concept tc2 = semaphoreModel.createConcept(URI.create("urn:foo:bar:tc2"), new Label("TC2", english));
		conceptSchemeExists.addTopConcept(tc2);
		assertEquals(conceptSchemeExists.getTopConcepts().size(), 2, "Size of top concepts after adding 1");


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
			fail("We shouldn't get here - creating a duplicate label");
		} catch (ModelException e) {
			assertEquals(e.getMessage(), "Attempting to create label for 'http://example.com/ConceptSchemeTest#ConceptScheme/MyFirstConceptScheme'. This already has a label in language 'en'");
		}
	}

	@Test
	public void testAddLiteralsToConceptScheme() throws URISyntaxException, ModelException {
		final String langNeutralLabel = "Lang Neutral Label";
		final String englishLabel = "English Label";
		final String italianLabel = "Labeli Italiano";
		ConceptScheme s = semaphoreModel.createConceptScheme(
				new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyConceptSchemeWithLangNeutralLit"),
				new Label(langNeutralLabel, null), null);
		s.addLabel(new Label(englishLabel, english));
		s.addLabel(new Label(italianLabel, italian));

		Label langNeutralLab = s.getLabel(null);
		assertNotNull(langNeutralLab, "Lang neutral label is not null");
		assertEquals(langNeutralLab.getValue(), langNeutralLabel, "Label value is correct");

		Label engLab = s.getLabel(english);
		assertNotNull(engLab, "Eng neutral label is not null");
		assertEquals(engLab.getValue(), englishLabel, "ENG Label value is correct");

		Label itaLab = s.getLabel(italian);
		assertNotNull(itaLab, "Eng neutral label is not null");
		assertEquals(itaLab.getValue(), italianLabel, "ITA Label value is correct");

	}

	@Test
	public void testAddingConceptSchemeWithGuid() throws URISyntaxException, ModelException {
		UUID mySchemeUUID = UUID.randomUUID();
		ConceptScheme s = semaphoreModel.createConceptScheme(
				new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyConceptSchemeWithGuid"),
				new Label("schemeWithGuid", null), mySchemeUUID);

		assertEquals(mySchemeUUID.toString(), s.getGuid());
	}

	@Test
	public void testAddingConceptScheme() throws URISyntaxException, ModelException {
		semaphoreModel.createConceptScheme(
				new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyCreatedConceptScheme"),
				new Label("hello", english), null);

		ConceptScheme createdConceptScheme = semaphoreModel.getConceptScheme(
				new URI("http://example.com/ConceptSchemeTest#ConceptScheme/MyCreatedConceptScheme"));
		assertNotNull(createdConceptScheme, "Concept Scheme has been created");
		
		Label labelEn = createdConceptScheme.getLabel(english); 
		assertEquals(labelEn.getValue(), "hello", "Created label Value");

		assertNotNull(createdConceptScheme.getGuid(),"GUID was created");

		Resource conceptSchemeRes = createdConceptScheme.getResource();
		assertEquals(conceptSchemeRes.getProperty(RDF.type).getObject().asResource(), SKOS.ConceptScheme,
				"Check concept scheme class is correct");

		createdConceptScheme.updateLabel(new Label("goodbye", english));
		Label updatedLabelEn = createdConceptScheme.getLabel(english); 
		assertEquals(updatedLabelEn.getValue(), "goodbye", "Updated label Value (en)");

		createdConceptScheme.setLabel(new Label("ciao", italian));
		Label setLabelIt = createdConceptScheme.getLabel(italian); 
		assertEquals(setLabelIt.getValue(), "ciao", "Set label Value (it)");
		assertEquals(createdConceptScheme.getLabel(english).getValue(), "goodbye",
				"English label Value not affected (en)");

		createdConceptScheme.setLabel(new Label("adios", italian));
		Label setLabelIt2 = createdConceptScheme.getLabel(italian); 
		assertEquals(setLabelIt2.getValue(), "adios", "Set(2) label Value (it)");
		assertEquals(createdConceptScheme.getLabel(english).getValue(), "goodbye",
				"English label Value not affected (en)");
		
		try {
			createdConceptScheme.deleteLabel(new Label("swizzle", english));
			fail("Delete undefined label should have thrown an exception");
		} catch (ModelException e) {
			assertEquals(e.getMessage(), "Attempting to delete label \"swizzle\"en for 'http://example.com/ConceptSchemeTest#ConceptScheme/MyCreatedConceptScheme'. This label does not exist");
		}

		createdConceptScheme.deleteLabel(new Label("goodbye", english));
		Label deletedLabelEn = createdConceptScheme.getLabel(english); 
		assertNull(deletedLabelEn, "Deleted label Value (en)");

		
		// We are "removing" a label that doesn't exist. This will do nothing if not english
		createdConceptScheme.removeLabel(new Label("goodbye", italian));
		Label removedLabelIt = createdConceptScheme.getLabel(italian); 
		assertEquals(removedLabelIt.getValue(), "adios", "Remove(1) label Value (it)");
		
		// We are "removing" a label that exists. 
		createdConceptScheme.removeLabel(new Label("adios", italian));
		Label removedLabelIt2 = createdConceptScheme.getLabel(italian); 
		assertNull(removedLabelIt2, "Remove(2) label Value (it)");
	}

}

package com.smartlogic.semaphoremodel;

import org.apache.jena.vocabulary.SKOS;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Set;

import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.FileAssert.fail;

public class TestConcept extends AbstractTest {

	private SemaphoreModel semaphoreModel;
	private final Language english = Language.getLanguage("en");
	private final Language italian = Language.getLanguage("it");
	private final Language german = Language.getLanguage("de");

	@BeforeTest
	public void loadModel() {
		semaphoreModel = new SemaphoreModel(new File("src/test/resources/TestModels/ConceptTestModel.ttl"));
	}

	@AfterTest
	public void dumpModel() throws FileNotFoundException {
		dumpModel(semaphoreModel);
	}

	@Test
	public void testCreateTopConcept() {
		try {
			ConceptScheme conceptScheme = semaphoreModel.getConceptScheme(URI.create("http://example.com/ConceptTestModel#ConceptScheme/MyConceptScheme"));
			Concept newTopConcept = semaphoreModel.createConcept(
					URI.create("http://example.com/ConceptTestModel#newTopConcept"),
					new Label("New Top Concept", english)
			);
			conceptScheme.addTopConcept(newTopConcept);
			conceptScheme = semaphoreModel.getConceptScheme(URI.create("http://example.com/ConceptTestModel#ConceptScheme/MyConceptScheme"));
		} catch (ModelException me) {
			fail("Model test failed", me);
		}
	}


	@Test
	public void testCreateNarrowerConcept() {
		try {
			ConceptScheme conceptScheme = semaphoreModel.getConceptScheme(URI.create("http://example.com/ConceptTestModel#ConceptScheme/MyConceptScheme"));
			Concept newTopConcept = semaphoreModel.createConcept(
					URI.create("http://example.com/ConceptTestModel#newTopConcept2"),
					new Label("New Top Concept 2", english)
			);
			conceptScheme.addTopConcept(newTopConcept);
			Concept newChildConcept = semaphoreModel.createConcept(
					URI.create("http://example.com/ConceptTestModel#newChildConcept"),
					new Label("New Child Concept", english));
			newTopConcept.addChild(newChildConcept);

			Set<Concept> parents = newChildConcept.selectPropertyConcepts(SKOS.broader);
			assertTrue(parents.size() == 1, "Failed to create narrower concept");
			assertEquals(parents.iterator().next().getGuid(), newTopConcept.getGuid());
		} catch (ModelException me) {
			fail("failed to create narrower concept", me);
		}
	}
}

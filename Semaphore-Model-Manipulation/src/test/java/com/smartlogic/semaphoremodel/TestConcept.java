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
			newTopConcept.addNarrower(newChildConcept);

			Set<Concept> parents = newChildConcept.selectPropertyConcepts(SKOS.broader);
			assertTrue(parents.size() == 1, "Failed to create narrower concept");
			assertEquals(parents.iterator().next().getGuid(), newTopConcept.getGuid());
		} catch (ModelException me) {
			fail("failed to create narrower concept", me);
		}
	}

	@Test
	public void testPrefLabels() {
		try {
			Concept c1 = semaphoreModel.createConcept(new URI("urn:foo:bar:pltc1"), new Label("pltc1", english));
			c1.addPrefLabel(new Label("c1 Deutsch", german));
			c1.addPrefLabel(new Label("c1 Lang Neutral", null));

			assertEquals(c1.getPrefLabelForLanguage(german).getValue(), "c1 Deutsch");
			assertEquals(c1.getPrefLabelForLanguage((String) null).getValue(), "c1 Lang Neutral");
			assertEquals(c1.getPrefLabelForLanguage(english).getValue(), "pltc1");

			System.out.println(c1.getPrefLabelForLanguage(german).getURI());
			assertEquals(c1.getPrefLabelForLanguage(german).getLanguageCode(), "de");
			assertEquals(c1.getPrefLabelForLanguage("").getLanguageCode(), "");
			assertEquals(c1.getPrefLabelForLanguage(english).getLanguageCode(), "en");

			assertTrue(null == c1.getPrefLabelForLanguage("kr"));
			assertTrue(null == c1.getPrefLabelForLanguage("it"));
		} catch (Throwable t) {
			fail("Test failed, caught throwable", t);
		}
	}

	@Test
	public void testAltLabels() {
		try {
			Concept c1 = semaphoreModel.createConcept(new URI("urn:foo:bar:talc1"), new Label("c1 pref label", english));
			c1.addAltLabel(new Label("talc1 English", english));
			c1.addAltLabel(new Label("talc1 Deutsch", german));
			c1.addAltLabel(new Label("talc1 Lang Neutral", null));

			Set<Label> altLabels = c1.getAltLabels();
			assertEquals(altLabels.size(), 3);

			c1.addAltLabel(new Label("talc1 Deutsch 2", german));
			assertEquals(c1.getAltLabels().size(), 4);

			c1.deleteAltLabel(new Label("talc1 Deutsch 2", german));
			assertEquals(c1.getAltLabels().size(), 3);
		} catch (Throwable t) {
			fail("Test failed, caught throwable", t);
		}
	}
}

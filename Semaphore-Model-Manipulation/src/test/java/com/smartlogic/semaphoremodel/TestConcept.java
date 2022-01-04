package com.smartlogic.semaphoremodel;

import org.apache.jena.vocabulary.SKOS;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.FileAssert.fail;

public class TestConcept extends AbstractTest {

	private final Language english = Language.getLanguage("en");
	private final Language italian = Language.getLanguage("it");
	private final Language german = Language.getLanguage("de");
	private final Language french = Language.getLanguage("fr");
	private final Language dutch = Language.getLanguage("nl");
	private final Language esperanto = Language.getLanguage("eo");
	private SemaphoreModel semaphoreModel;

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
			assertEquals(conceptScheme.getURI().toString(), "http://example.com/ConceptTestModel#ConceptScheme/MyConceptScheme");
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
			Assert.assertEquals(parents.size(), 1, "Failed to create narrower concept");
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

			assertNull(c1.getPrefLabelForLanguage("kr"));
			assertNull(c1.getPrefLabelForLanguage("it"));
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

	@Test
	public void testBooleanMetadata() {
		try {
			URI mdBool1 = URI.create("http://test/md-boolean-1");
			semaphoreModel.createBooleanMetadataType(mdBool1, new Label("MD Boolean 1", null));
			assertNotNull(semaphoreModel.getMetadataType(mdBool1));
			Concept c1 = semaphoreModel.createConcept(new URI("urn:foo:bar:test-bmd-1"), new Label("metadata test 1", english));
			BooleanMetadataType bmt = (BooleanMetadataType) semaphoreModel.getMetadataType(mdBool1);
			c1.addMetadata(bmt, true);

			assertTrue(c1.getMetadata(bmt).contains(true));
			assertFalse(c1.getMetadata(bmt).contains(false));

			c1.removeAllMetadata(bmt);
			assertFalse(c1.getMetadata(bmt).contains(true));
			assertFalse(c1.getMetadata(bmt).contains(false));
		} catch (Throwable t) {
			fail("Test failed, caught throwable", t);
		}
	}

	@Test
	public void testDateMetadata() {

		try {
			URI mdDate1 = URI.create("http://test/md-date-1");
			semaphoreModel.createDateMetadataType(mdDate1, new Label("MD Date 1", null));
			assertNotNull(semaphoreModel.getMetadataType(mdDate1));
			Concept c1 = semaphoreModel.createConcept(new URI("urn:foo:bar:test-dmd-1"), new Label("metadata test 1", english));
			CalendarMetadataType cmt = (CalendarMetadataType) semaphoreModel.getMetadataType(mdDate1);
			c1.addMetadata(cmt, LocalDate.parse("2020-10-10"));
			c1.addMetadata(cmt, LocalDate.parse("2022-01-01"));
			c1.addMetadata(cmt, LocalDate.parse("2022-07-15"));

			assertTrue(c1.getMetadata(cmt).contains(LocalDate.parse("2020-10-10")));
			assertTrue(c1.getMetadata(cmt).contains(LocalDate.parse("2022-01-01")));
			assertTrue(c1.getMetadata(cmt).contains(LocalDate.parse("2022-07-15")));
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2020-10-31")));
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2022-07-14")));

			c1.removeMetadata(cmt, LocalDate.parse("2020-10-10"));
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2020-10-10")));
			assertTrue(c1.getMetadata(cmt).contains(LocalDate.parse("2022-01-01")));
			assertTrue(c1.getMetadata(cmt).contains(LocalDate.parse("2022-07-15")));

			c1.removeMetadata(cmt, LocalDate.parse("2022-07-15"));
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2020-10-10")));
			assertTrue(c1.getMetadata(cmt).contains(LocalDate.parse("2022-01-01")));
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2022-07-15")));

			c1.removeAllMetadata(cmt);
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2020-10-10")));
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2022-01-01")));
			assertFalse(c1.getMetadata(cmt).contains(LocalDate.parse("2022-07-15")));

		} catch (Throwable t) {
			fail("Test failed, caught throwable", t);
		}
	}

	@Test
	public void testStringMetadata() {

		try {
			URI mdStr1 = URI.create("http://test/md-string-1");
			semaphoreModel.createStringMetadataType(mdStr1, new Label("MD String 1", null));
			assertNotNull(semaphoreModel.getMetadataType(mdStr1));
			Concept c1 = semaphoreModel.createConcept(new URI("urn:foo:bar:test-smd-1"), new Label("metadata test 1", english));
			StringMetadataType smt = (StringMetadataType) semaphoreModel.getMetadataType(mdStr1);
			c1.addMetadata(smt, "My String Metadata 1 EN", english);
			c1.addMetadata(smt, "My String Metadata 2 EN", english);
			c1.addMetadata(smt, "My String Metadata 3 EN", english);
			c1.addMetadata(smt, "My String Metadata FR", french);
			c1.addMetadata(smt, "My String Metadata 1 DE", german);
			c1.addMetadata(smt, "My String Metadata 2 DE", german);
			c1.addMetadata(smt, "My String Metadata IT", italian);

			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 1 EN"));
			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 3 EN"));
			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 2 EN"));
			assertTrue(c1.getMetadata(smt, french).contains("My String Metadata FR"));
			assertTrue(c1.getMetadata(smt, german).contains("My String Metadata 2 DE"));
			assertTrue(c1.getMetadata(smt, german).contains("My String Metadata 1 DE"));
			assertTrue(c1.getMetadata(smt, italian).contains("My String Metadata IT"));
			assertTrue(c1.getMetadata(smt, null).isEmpty());
			assertTrue(c1.getMetadata(smt, dutch).isEmpty());
			assertTrue(c1.getMetadata(smt, esperanto).isEmpty());

			c1.addMetadata(smt, "My String Metadata 2 L-N", null);
			c1.addMetadata(smt, "My String Metadata 1 L-N", null);

			/* I need some way to confirm state changes did not have side effects */
			assertTrue(
				c1.getMetadata(smt, null).contains(
					"My String Metadata 2 L-N"));

			/* I need some way to confirm state changes did not have side effects */
			c1.removeMetadata(smt, "My String Metadata IT", italian);
			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 1 EN"));
			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 3 EN"));
			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 2 EN"));
			assertTrue(c1.getMetadata(smt, french).contains("My String Metadata FR"));
			assertTrue(c1.getMetadata(smt, german).contains("My String Metadata 2 DE"));
			assertTrue(c1.getMetadata(smt, german).contains("My String Metadata 1 DE"));
			assertFalse(c1.getMetadata(smt, italian).contains("My String Metadata IT"));
			assertFalse(c1.getMetadata(smt, null).isEmpty());
			assertEquals(c1.getMetadata(smt, null).size(), 2);
			assertTrue(c1.getMetadata(smt, dutch).isEmpty());
			assertTrue(c1.getMetadata(smt, esperanto).isEmpty());

			c1.removeMetadata(smt, "My String Metadata 2 EN", english);
			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 1 EN"));
			assertTrue(c1.getMetadata(smt, english).contains("My String Metadata 3 EN"));
			assertFalse(c1.getMetadata(smt, english).contains("My String Metadata 2 EN"));
			assertTrue(c1.getMetadata(smt, french).contains("My String Metadata FR"));
			assertTrue(c1.getMetadata(smt, german).contains("My String Metadata 2 DE"));
			assertTrue(c1.getMetadata(smt, german).contains("My String Metadata 1 DE"));
			assertFalse(c1.getMetadata(smt, italian).contains("My String Metadata IT"));
			assertFalse(c1.getMetadata(smt, null).isEmpty());
			assertEquals(c1.getMetadata(smt, null).size(), 2);
			assertTrue(c1.getMetadata(smt, dutch).isEmpty());
			assertTrue(c1.getMetadata(smt, esperanto).isEmpty());

			// nuke all of it
			c1.removeAllMetadata(smt);
			assertFalse(c1.getMetadata(smt, english).contains("My String Metadata 1 EN"));
			assertFalse(c1.getMetadata(smt, english).contains("My String Metadata 3 EN"));
			assertFalse(c1.getMetadata(smt, english).contains("My String Metadata 2 EN"));
			assertFalse(c1.getMetadata(smt, french).contains("My String Metadata FR"));
			assertFalse(c1.getMetadata(smt, german).contains("My String Metadata 2 DE"));
			assertFalse(c1.getMetadata(smt, german).contains("My String Metadata 1 DE"));
			assertFalse(c1.getMetadata(smt, italian).contains("My String Metadata IT"));
			assertEquals(c1.getMetadata(smt, null).size(), 0);
			assertTrue(c1.getMetadata(smt, dutch).isEmpty());
			assertTrue(c1.getMetadata(smt, esperanto).isEmpty());


		} catch (Throwable t) {
			fail("Test failed, caught throwable", t);
		}
	}

	@Test
	public void testIntegerMetadata() {
		try {
			URI mdInt1 = URI.create("http://test/md-integer-1");
			semaphoreModel.createIntegerMetadataType(mdInt1, new Label("MD Integer 1", null));
			assertNotNull(semaphoreModel.getMetadataType(mdInt1));

			Concept c1 = semaphoreModel.createConcept(new URI("urn:foo:bar:test-imd-1"), new Label("metadata test 1", english));
			IntegerMetadataType imt = (IntegerMetadataType) semaphoreModel.getMetadataType(mdInt1);
			c1.addMetadata(imt, 200);
			c1.addMetadata(imt, 500);
			assertTrue(c1.getMetadata(imt).contains(200));
			assertTrue(c1.getMetadata(imt).contains(500));
			assertFalse(c1.getMetadata(imt).contains(501));
			assertFalse(c1.getMetadata(imt).contains(-1));

			c1.removeMetadata(imt, 500);
			assertTrue(c1.getMetadata(imt).contains(200));
			assertFalse(c1.getMetadata(imt).contains(500));
			assertFalse(c1.getMetadata(imt).contains(501));
			assertFalse(c1.getMetadata(imt).contains(-1));
		} catch (Throwable t) {
			fail("Test failed, caught throwable", t);
		}
	}
}

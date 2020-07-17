package com.smartlogic.semaphoremodel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class TestConceptClass extends AbstractTest {

	private SemaphoreModel semaphoreModel;
	private Language english = Language.getLanguage("en");

	@BeforeTest
	public void loadModel() {
		semaphoreModel = new SemaphoreModel(new File("src/test/resources/TestModels/ConceptClassTest.ttl"));
	}

	@AfterTest
	public void dumpModel() throws FileNotFoundException {
		dumpModel(semaphoreModel);
	}

	@Test
	public void testGetConceptClass() {
		try {
			Model m = ModelFactory.createDefaultModel();
			ConceptClass cc = semaphoreModel.getConceptClass(
					new Label("Brand", Language.getLanguage("en")));
			assertEquals(cc.getResource(),
					semaphoreModel.createResource(URI.create("http://proto.smartlogic.com/example#Brand")));
		} catch (ModelException me) {
			fail("Exception caught", me);
		}
	}

	@Test
	public void testAddConceptClass() {
		try {
			ConceptClass cc = semaphoreModel.createConceptClass(URI.create("http://foobar.com/foo/MyClass"),
					new Label("MyClass", Language.getLanguage("en")));
			ConceptClass fetchedCc = semaphoreModel.getConceptClass(
					new Label("MyClass", Language.getLanguage("en")));
			assertEquals(cc.getResource(), fetchedCc.getResource());
		} catch (ModelException me) {
			fail("Exception caught", me);
		}
	}

	@Test
	public void testAddConceptClassFromSuper() {
		try {
			ConceptClass cc = semaphoreModel.getConceptClass(new Label("MyClass", Language.getLanguage("en")));
			if (null == cc) {
				cc = semaphoreModel.createConceptClass(URI.create("http://foobar.com/foo/MyClass"),
						new Label("MyClass", Language.getLanguage("en")));
			}
			ConceptClass cc2 = semaphoreModel.createConceptClass(URI.create("http://foobar.com/foo/MyClass2"),
					new Label("MyClass2", Language.getLanguage("en")), cc.getResource());
			ConceptClass cc2Fetched = semaphoreModel.getConceptClass(
					new Label("MyClass2", Language.getLanguage("en")));
			assertEquals(cc2.getResource(), cc2Fetched.getResource());
			assertEquals(cc2.getURI(), cc2Fetched.getURI());
			ConceptClass ccFetched = semaphoreModel.getConceptClassParent(cc2);
			assertEquals(cc.getURI(), ccFetched.getURI());
		} catch (ModelException me) {
			fail("Exception caught", me);
		}
	}

}

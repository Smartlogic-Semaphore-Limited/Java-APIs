package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;

import org.testng.annotations.Test;

public class ClassifySimpleTest extends ClassificationTestCase {

	@Test
	public void testSimple() throws ClassificationException {

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis ";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new Body(body), new Title(title)).getAllClassifications();
		assertEquals(4, classificationScores.get("IPSV-Health, well-being and care").size(), "run 1 - IPSV");
	}

	@Test
	public void testSimpleWithFileName() throws ClassificationException {

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis \0xc3\0xb3";
		String filename = "A label to go in audit log";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new FileName(filename), new Body(body), new Title(title)).getAllClassifications();
		assertEquals(4, classificationScores.get("IPSV-Health, well-being and care").size(), "run 1 - IPSV");
	}

	@Test
	public void testSimpleWithResults() throws ClassificationException {

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis \0xc3\0xb3";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new Body(body), new Title(title)).getAllClassifications();
		assertEquals(4, classificationScores.get("IPSV-Health, well-being and care").size(), "run 1 - IPSV");
	}

}

package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class ClassifyNullTest extends ClassificationTestCase {

	@Test
	public void testNull() throws ClassificationException {
		
		String title = "This is the document title";
		String body = "";
		Result result1 = classificationClient.getClassifiedDocument(new Body(body), new Title(title));
		assertEquals(0, result1.getAllClassifications().size(), "No results 1");
		Result result2 = classificationClient.getClassifiedDocument(new byte[0], "cheeey.pdf");
		assertEquals(0, result2.getAllClassifications().size(), "No results 2");
	}

}

package com.smartlogic.classificationserver.client;


public class ClassifyNullTest extends ClassificationTestCase {

	public void testNull() throws ClassificationException {
		
		String title = "This is the document title";
		String body = "";
		Result result1 = classificationClient.getClassifiedDocument(new Body(body), new Title(title));
		assertEquals("No results 1", 0, result1.getAllClassifications().size());
		Result result2 = classificationClient.getClassifiedDocument(new byte[0], "cheeey.pdf");
		assertEquals("No results 2", 0, result2.getAllClassifications().size());
	}

}

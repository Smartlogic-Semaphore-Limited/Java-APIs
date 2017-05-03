package com.smartlogic.classificationserver.client;

import java.io.File;

public class ClassifySimpleMultipleTest extends ClassificationTestCase {

	public void testSimpleMultiple() throws ClassificationException {
		

		File file = new File("src/test/resources/data/SampleData.txt");
		for (int i = 0; i < 5; i++) {
			Result result = classificationClient.getClassifiedDocument(file, "text");
			assertEquals("Result " + i, 3, result.getAllClassifications().size());
		}
		
	}

}

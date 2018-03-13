package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.testng.annotations.Test;

public class ClassifySimpleMultipleTest extends ClassificationTestCase {

	@Test
	public void testSimpleMultiple() throws ClassificationException {

		File file = new File("src/test/resources/data/SampleData.txt");
		for (int i = 0; i < 5; i++) {
			Result result = classificationClient.getClassifiedDocument(file, "text");
			assertEquals(3, result.getAllClassifications().size(), "Result " + i);
		}
		
	}

}

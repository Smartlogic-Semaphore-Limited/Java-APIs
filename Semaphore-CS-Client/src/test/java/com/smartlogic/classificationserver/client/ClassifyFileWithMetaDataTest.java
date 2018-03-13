package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.File;

import org.testng.annotations.Test;

public class ClassifyFileWithMetaDataTest extends ClassificationTestCase {

	@Test
	public void testClassifyFileWithMetaData() {
		
		try {
			File file = new File("src/test/resources/data/SampleData.txt");
			Result result = classificationClient.getClassifiedDocument(file, null);
			assertEquals("TEXT", result.getMetadata().get("Type"), "Type");
		} catch (Exception e) {
			fail("Exception encountered: " + e.getMessage());
		}
	}
}

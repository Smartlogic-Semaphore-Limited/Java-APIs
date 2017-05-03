package com.smartlogic.classificationserver.client;

import java.io.File;

public class ClassifyFileWithMetaDataTest extends ClassificationTestCase {

	public void testClassifyFileWithMetaData() {
		
		try {
			
			File file = new File("src/test/resources/data/SampleData.txt");

			Result result = classificationClient.getClassifiedDocument(file, null);
			assertEquals("Type", "TEXT", result.getMetadata().get("Type"));
			
		} catch (Exception e) {
			fail("Exception encountered: " + e.getMessage());
		}
	}


}

package com.smartlogic.classificationserver.client;

import java.io.File;

public class ClassifyFileWithMetaDataTest extends ClassificationTestCase {

	public void testClassifyFileWithMetaData() {
		
		try {
			
			File file = new File("src/test/resources/data/44157109.pdf");

			Result result = classificationClient.getClassifiedDocument(file, null);
			assertEquals("Number Of Pages", "5", result.getMetadata().get("Number Of Pages"));
			assertEquals("Type", "PDF", result.getMetadata().get("Type"));
			assertEquals("Creator", "PScript5.dll Version 5.2.2", result.getMetadata().get("Creator"));
			assertEquals("Author", "UKMXJ", result.getMetadata().get("Author"));
			assertEquals("Producer", "Acrobat Distiller 8.1.0 (Windows)", result.getMetadata().get("Producer"));
			assertEquals("title/document_title", "Microsoft Word - Tomk Dec08", result.getMetadata().get("title/document_title"));
			
		} catch (Exception e) {
			fail("Exception encountered: " + e.getMessage());
		}
	}


}

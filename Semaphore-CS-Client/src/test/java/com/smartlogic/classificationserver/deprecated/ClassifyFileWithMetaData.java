package com.smartlogic.classificationserver.deprecated;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.smartlogic.classificationserver.client.ClassificationTestCase;

public class ClassifyFileWithMetaData extends ClassificationTestCase {

	@SuppressWarnings("deprecation")
	public void testClassifyFileWithMetaData() {
		
		try {
			
			File file = new File("src/test/resources/data/44157109.pdf");

			Map<String, String> outMetadata = new HashMap<String, String>();
			classificationClient.classifyFileWithMetadata(file, null, null, outMetadata);
			
			assertEquals("Number Of Pages", "5", outMetadata.get("Number Of Pages"));
			assertEquals("Type", "PDF", outMetadata.get("Type"));
			assertEquals("Creator", "PScript5.dll Version 5.2.2", outMetadata.get("Creator"));
			assertEquals("Author", "UKMXJ", outMetadata.get("Author"));
			assertEquals("Producer", "Acrobat Distiller 8.1.0 (Windows)", outMetadata.get("Producer"));
			assertEquals("title/document_title", "Microsoft Word - Tomk Dec08", outMetadata.get("title/document_title"));
			
			for (String key: outMetadata.keySet()) {
				System.out.println(key + ":" + outMetadata.get(key));
			}
			
			
		} catch (Exception e) {
			System.err.println("Exception encountered: " + e.getMessage());
		}
	}


}

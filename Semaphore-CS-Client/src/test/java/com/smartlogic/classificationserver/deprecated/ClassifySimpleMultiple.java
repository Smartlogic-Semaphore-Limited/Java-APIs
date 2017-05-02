package com.smartlogic.classificationserver.deprecated;

import java.io.File;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;

public class ClassifySimpleMultiple extends ClassificationTestCase {

	@SuppressWarnings("deprecation")
	public void testSimpleMultiple() throws ClassificationException {
		

		File file = new File("src/test/resources/data/44157109.pdf");
		for (int i = 0; i < 25; i++) {
			classificationClient.classifyFile(file, "text");
		}
		
	}

}

package com.smartlogic.classificationserver.deprecated;

import com.smartlogic.classificationserver.client.Body;
import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Title;


public class ClassifyNull extends ClassificationTestCase {

	@SuppressWarnings("deprecation")
	public void testNull() throws ClassificationException {
		
		String title = "This is the document title";
		String body = "";
		classificationClient.classifyDocument(new Body(body), new Title(title));
		
		classificationClient.classifyBinary(new byte[0], "cheeey.pdf");
	}

}

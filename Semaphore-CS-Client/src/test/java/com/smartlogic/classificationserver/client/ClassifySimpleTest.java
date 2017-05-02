package com.smartlogic.classificationserver.client;

import java.util.Collection;
import java.util.Map;

public class ClassifySimpleTest extends ClassificationTestCase {

	public void testSimple() throws ClassificationException {

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis ";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new Body(body), new Title(title)).getAllClassifications();
		assertEquals("run 1 - IPSV", 1, classificationScores.get("IPSV").size());
		assertEquals("run 1 - IPSV_ID", 1, classificationScores.get("IPSV_ID").size());
		assertEquals("run 1 - IPSV_RAW", 1, classificationScores.get("IPSV_RAW").size());
	}

	public void testSimpleWithFileName() throws ClassificationException {

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis \0xc3\0xb3";
		String filename = "A label to go in audit log";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new FileName(filename), new Body(body), new Title(title)).getAllClassifications();
		assertEquals("run 2 - IPSV", 1, classificationScores.get("IPSV").size());
		assertEquals("run 2 - IPSV_ID", 1, classificationScores.get("IPSV_ID").size());
		assertEquals("run 2 - IPSV_RAW", 1, classificationScores.get("IPSV_RAW").size());
	}

	public void testSimpleWithResults() throws ClassificationException {

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis \0xc3\0xb3";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new Body(body), new Title(title)).getAllClassifications();
		assertEquals("run 3 - IPSV", 1, classificationScores.get("IPSV").size());
		assertEquals("run 3 - IPSV_ID", 1, classificationScores.get("IPSV_ID").size());
		assertEquals("run 3 - IPSV_RAW", 1, classificationScores.get("IPSV_RAW").size());
	}

}

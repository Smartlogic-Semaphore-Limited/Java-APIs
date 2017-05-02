package com.smartlogic.classificationserver.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;

public class ClassifyUrlTest extends ClassificationTestCase {

	@Test
	public void testClassifyUrl() throws MalformedURLException, ClassificationException {

		Map<String, Collection<ClassificationScore>> classificationScores1 = classificationClient.getClassifiedDocument(new URL("http://www.bsad.org/0506/reports/ipswich/ar1.html")).getAllClassifications();
		assertEquals("run 1 - IPSV", 4, classificationScores1.get("IPSV").size());
		assertEquals("run 1 - IPSV_ID", 4, classificationScores1.get("IPSV_ID").size());
		assertEquals("run 1 - IPSV_RAW", 4, classificationScores1.get("IPSV_RAW").size());

		Map<String, Collection<ClassificationScore>> classificationScores2 = classificationClient.getClassifiedDocument(new URL("http://www.bsad.org/0506/reports/ipswich/ar1.html"), new Title("Abandoned Vehicles"), null).getAllClassifications();
		assertEquals("run 2 - IPSV", 5, classificationScores2.get("IPSV").size());
		assertEquals("run 2 - IPSV_ID", 5, classificationScores2.get("IPSV_ID").size());
		assertEquals("run 2 - IPSV_RAW", 5, classificationScores2.get("IPSV_RAW").size());
	}

}

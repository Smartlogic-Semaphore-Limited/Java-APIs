package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import org.testng.annotations.Test;


public class ClassifyUrlTest extends ClassificationTestCase {

	@Test
	public void testClassifyUrl() throws MalformedURLException, ClassificationException {

		Map<String, Collection<ClassificationScore>> classificationScores1 = classificationClient.getClassifiedDocument(new URL("http://www.bsad.org/0506/reports/ipswich/ar1.html")).getAllClassifications();
		assertEquals(4, classificationScores1.get("IPSV").size(), "run 1 - IPSV");
		assertEquals(4, classificationScores1.get("IPSV_ID").size(), "run 1 - IPSV_ID");
		assertEquals(4, classificationScores1.get("IPSV_RAW").size(), "run 1 - IPSV_RAW");

		Map<String, Collection<ClassificationScore>> classificationScores2 = classificationClient.getClassifiedDocument(new URL("http://www.bsad.org/0506/reports/ipswich/ar1.html"), new Title("Abandoned Vehicles"), null).getAllClassifications();
		assertEquals(5, classificationScores2.get("IPSV").size(), "run 2 - IPSV");
		assertEquals(5, classificationScores2.get("IPSV_ID").size(), "run 2 - IPSV_ID");
		assertEquals(5, classificationScores2.get("IPSV_RAW").size(), "run 2 - IPSV_RAW");
	}

}

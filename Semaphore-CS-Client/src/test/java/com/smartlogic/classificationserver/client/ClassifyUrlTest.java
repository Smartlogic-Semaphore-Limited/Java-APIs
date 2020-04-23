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
		assertEquals(11, classificationScores1.get("IPSV-Information and communication").size(), "run 1 - IPSV-Information and communication");
		System.out.println(classificationScores1.get("IPSV-Public order, justice and rights"));
		assertEquals(6, classificationScores1.get("IPSV-Public order, justice and rights").size(), "run 1 - IPSV-Public order, justice and rights");
		assertEquals(2, classificationScores1.get("IPSV-Transport and infrastructure").size(), "run 1 - IPSV-Transport and infrastructure");

		Map<String, Collection<ClassificationScore>> classificationScores2 = classificationClient.getClassifiedDocument(new URL("http://www.bsad.org/0506/reports/ipswich/ar1.html"), new Title("Abandoned Vehicles"), null).getAllClassifications();
		assertEquals(11, classificationScores2.get("IPSV-Information and communication").size(), "run 2 - IPSV-Information and communication");
		assertEquals(6, classificationScores2.get("IPSV-Public order, justice and rights").size(), "run 2 - IPSV-Public order, justice and rights");
		assertEquals(4, classificationScores2.get("IPSV-Transport and infrastructure").size(), "run 2 - IPSV-Transport and infrastructure");
	}

}

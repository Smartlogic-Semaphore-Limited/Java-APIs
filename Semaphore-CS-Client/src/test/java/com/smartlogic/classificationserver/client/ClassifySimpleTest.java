package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

public class ClassifySimpleTest extends ClassificationTestCase {

	@Test
	public void testSimple() throws ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSimple1.xml"))));

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis ";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new Body(body), new Title(title)).getAllClassifications();
		assertEquals(3, classificationScores.get("IPSV-Health, well-being and care").size(), "run 1 - IPSV");
	}

	@Test
	public void testSimpleWithFileName() throws ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSimple1.xml"))));

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis";
		String filename = "A label to go in audit log";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new FileName(filename), new Body(body), new Title(title)).getAllClassifications();
		assertEquals(3, classificationScores.get("IPSV-Health, well-being and care").size(), "run 1 - IPSV");
	}

	@Test
	public void testSimpleWithResults() throws ClassificationException {

		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSimple1.xml"))));

		String title = "This is the document title";
		String body = "This is the document body it talks about softball and soft drinks as well as soft tennis \0xc3\0xb3";
		Map<String, Collection<ClassificationScore>> classificationScores = classificationClient.getClassifiedDocument(new Body(body), new Title(title)).getAllClassifications();
		assertEquals(3, classificationScores.get("IPSV-Health, well-being and care").size(), "run 1 - IPSV");
	}

}

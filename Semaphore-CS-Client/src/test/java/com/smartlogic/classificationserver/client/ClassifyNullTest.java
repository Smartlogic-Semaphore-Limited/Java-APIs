package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

public class ClassifyNullTest extends ClassificationTestCase {

	@Test
	public void testNull() throws ClassificationException {

		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseNoResults.xml"))));

		String title = "This is the document title";
		String body = "";
		Result result1 = classificationClient.getClassifiedDocument(new Body(body), new Title(title));
		assertEquals(0, result1.getAllClassifications().size(), "No results 1");
		Result result2 = classificationClient.getClassifiedDocument(new byte[0], "cheeey.pdf");
		assertEquals(0, result2.getAllClassifications().size(), "No results 2");
	}

}

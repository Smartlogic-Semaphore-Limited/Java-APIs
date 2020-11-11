package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

public class TestHash extends ClassificationTestCase {

	@Test
	public void testHash() throws ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseCheeseCrackers.xml"))));

		Result result = classificationClient.getClassifiedDocument(new Body("Wibble bot"), new Title("Cheese crackers"));
		assertEquals("0a5cb15882dbf7ff35febf695e14e972", result.getHash());

	}

}

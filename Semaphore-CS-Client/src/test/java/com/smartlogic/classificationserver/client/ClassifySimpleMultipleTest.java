package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import java.io.File;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

public class ClassifySimpleMultipleTest extends ClassificationTestCase {

	@Test
	public void testSimpleMultiple() throws ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSampleData.xml"))));

		File file = new File("src/test/resources/data/SampleData.txt");
		for (int i = 0; i < 5; i++) {
			Result result = classificationClient.getClassifiedDocument(file, "text");
			assertEquals(7, result.getAllClassifications().size(), "Result " + i);
		}
		
	}

}

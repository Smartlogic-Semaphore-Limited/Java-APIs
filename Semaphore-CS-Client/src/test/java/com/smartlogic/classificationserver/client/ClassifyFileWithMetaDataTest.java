package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import java.io.File;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class ClassifyFileWithMetaDataTest extends ClassificationTestCase {

	@Test
	public void testClassifyFileWithMetaData() {

		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSampleData.xml"))));

		try {
			File file = new File("src/test/resources/data/SampleData.txt");
			Result result = classificationClient.getClassifiedDocument(file, null);
			assertEquals("TEXT (4003)", result.getMetadata().get("Type"), "Type");
		} catch (Exception e) {
			fail("Exception encountered: " + e.getMessage());
		}
	}
}

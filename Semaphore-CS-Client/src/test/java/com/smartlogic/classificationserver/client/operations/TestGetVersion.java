package com.smartlogic.classificationserver.client.operations;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import org.testng.annotations.Test;

import java.text.ParseException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertTrue;

public class TestGetVersion extends ClassificationTestCase {

	@Test
	public void testGetVersion() throws ParseException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseGetVersion.xml"))));

		String version = classificationClient.getVersion();

		assertTrue(version.indexOf("Classification Server") > -1, "Version");
		assertTrue(version.indexOf("built on") > -1, "Version");
	}
}

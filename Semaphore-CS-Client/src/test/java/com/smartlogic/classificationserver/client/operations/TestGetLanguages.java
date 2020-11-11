package com.smartlogic.classificationserver.client.operations;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Language;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.fail;

public class TestGetLanguages extends ClassificationTestCase {

	@Test
	public void testGetLanguages() throws ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseGetLanguages.xml"))));

		try {
			@SuppressWarnings("unused")
			Collection<Language> languages = classificationClient.getLanguages();
		} catch (Exception e) {
			fail("Exception thrown getting languages: " + e.getMessage());
		}
	}

}

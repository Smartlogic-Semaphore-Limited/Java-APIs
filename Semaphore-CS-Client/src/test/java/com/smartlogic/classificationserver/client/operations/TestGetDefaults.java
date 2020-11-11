package com.smartlogic.classificationserver.client.operations;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import com.smartlogic.classificationserver.client.Parameter;
import org.testng.annotations.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class TestGetDefaults extends ClassificationTestCase {

	@Test
	public void testGetLanguages() throws ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseGetDefaults.xml"))));

		try {
			Map<String, Parameter> defaults = classificationClient.getDefaults();
			
			Parameter charCountCutoff = defaults.get("charcountcutoff");
			assertEquals("500000", charCountCutoff.getValue());
		} catch (Exception e) {
			fail("Exception thrown getting defaults: " + e.getMessage());
		}
	}

}

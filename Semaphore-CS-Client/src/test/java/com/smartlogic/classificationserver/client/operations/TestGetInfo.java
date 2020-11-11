package com.smartlogic.classificationserver.client.operations;

import com.smartlogic.classificationserver.client.CSInfo;
import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.fail;

public class TestGetInfo extends ClassificationTestCase {

	@Test
	public void testGetInfo() throws ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseGetInfo.xml"))));

		try {
			CSInfo csInfo = classificationClient.getInfo();
			System.out.println(csInfo);
		} catch (Exception e) {
			fail("Exception thrown getting info: " + e.getMessage());
		}
	}

}

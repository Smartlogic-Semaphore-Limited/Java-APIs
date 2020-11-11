package com.smartlogic.ses.client;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestConceptMapping extends SESServerMockTestCase {

	private static SESClient sesClient;

	public void setUp() {
		wireMockRule.start();
		if (sesClient == null) {
			sesClient = ConfigUtil.getSESClient();
		}
	}

	public void tearDown() {
		wireMockRule.stop();
	}

	public void testConceptMappingBusiness() throws Exception {
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=conceptmap&query=business"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/ses/sesResponseConceptsBusiness.xml"))));

		Map<String, Term> terms = sesClient.getMappedConcepts("business");
		assertTrue(terms.containsKey("729c064f-166c-4924-b1b7-ffe38484142d"));
		Term t = terms.get("729c064f-166c-4924-b1b7-ffe38484142d");
		assertTrue(t.getId().getValue().equals("729c064f-166c-4924-b1b7-ffe38484142d"));
		assertEquals("Business and industry", t.getName().getValue());
		assertTrue(terms.containsKey("4b98f444-f14b-5fde-b2d0-7d79f8e5adb5"));
		t = terms.get("4b98f444-f14b-5fde-b2d0-7d79f8e5adb5");
		assertEquals("Business women", t.getName().getValue());
	}
}

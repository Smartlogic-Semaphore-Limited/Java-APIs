package com.smartlogic.ses.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestTermsXML extends SESServerMockTestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	private static String xml;

	public void setUp() throws Exception {
		wireMockRule.start();
		if (sesClient == null) {
			sesClient = ConfigUtil.getSESClient();
			xml = readFileToString("src/test/resources/TestTerm.xml");
		}
	}

	public void tearDown() {
		wireMockRule.stop();
	}

	public void testMarshal() throws Exception {
		wireMockRule.stubFor(get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=term&term=Livestock+markets"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/ses/sesResponseTermLivestockMarkets.xml"))));

		Map<String, Term> terms = sesClient.getTermDetailsByName("Livestock markets");

		Term term = terms.get("fa99b82b-a642-54e0-be7d-25d7b26ae53c");

		XMLifier<Term> xmlifier = new XMLifier<Term>(Term.class);

		String xmlResult = xmlifier.objectAsXML(term);
		System.out.println(xmlResult);
		assertTrue("Marshalling of term", xmlResult.startsWith("<?xml"));
	}

	@SuppressWarnings("deprecation")
	public void testUnmarshall() throws Exception {
		XMLifier<Term> xmlifier = new XMLifier<Term>(Term.class);
		Term term = xmlifier.objectFromXML(xml);

		assertEquals("Term values", "animal markets", term.getSynonyms().getSynonyms().get(0).getValue());
		assertEquals("Term values", "animal markets", term.getSynonymsList().get(0).getSynonyms().get(0).getValue());
		assertEquals("Term values", "Cattle Markets", term.getSynonymsList().get(0).getSynonyms().get(1).getValue());
	}

	public void testStructure() throws Exception {
		wireMockRule.stubFor(get(urlEqualTo("/ses/IPSV"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/ses/sesResponseStructure.xml"))));

		OMStructure structure = sesClient.getStructure();

		XMLifier<OMStructure> xmlifier = new XMLifier<OMStructure>(OMStructure.class);
		System.out.println(xmlifier.objectAsXML(structure));

	}
}

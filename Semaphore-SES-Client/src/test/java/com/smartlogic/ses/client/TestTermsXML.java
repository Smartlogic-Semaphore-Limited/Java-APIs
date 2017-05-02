package com.smartlogic.ses.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestTermsXML extends TestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	private static String xml;

	public void setUp() throws Exception {
		if (sesClient == null) {
			sesClient = new SESClient();
			sesClient.setConnectionTimeoutMS(0);
			sesClient.setHost("mlhostdev02");
			sesClient.setOntology("disp_taxonomy");
			sesClient.setPath("/ses");
			sesClient.setPort(80);
			sesClient.setProtocol("http");
			sesClient.setSocketTimeoutMS(0);
//			sesClient.setLanguage("English");


			BufferedReader reader = new BufferedReader(new FileReader(new File("src/test/resources/TestTerm.xml")));
			String data;
			StringBuilder stringBuilder = new StringBuilder();
			while ((data = reader.readLine()) != null) {
				stringBuilder.append(data);
				stringBuilder.append("\n");
			}
			reader.close();
			xml = stringBuilder.toString();

		}
	}


	public void testMarshal() throws Exception {
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
		OMStructure structure = sesClient.getStructure();

		XMLifier<OMStructure> xmlifier = new XMLifier<OMStructure>(OMStructure.class);
		System.out.println(xmlifier.objectAsXML(structure));

	}


}

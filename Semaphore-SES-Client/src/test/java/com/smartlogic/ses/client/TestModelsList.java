package com.smartlogic.ses.client;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.smartlogic.ses.client.exceptions.SESException;

public class TestModelsList extends PrintingTestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	public void setUp() {
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
		}
	}


	public void testListModels() throws SESException {
		logger.info("testListModels - entry");
		Collection<Model> models = sesClient.listModels();
		assertEquals("Models count",1, models.size());
		assertTrue("First model", models.contains(new Model("disp_taxonomy")));

		logger.info("testListModels - exit");
	}


}

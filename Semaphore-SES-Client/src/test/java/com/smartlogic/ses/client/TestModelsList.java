package com.smartlogic.ses.client;

import com.smartlogic.ses.client.exceptions.SESException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

public class TestModelsList extends PrintingTestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	private static SESClient sesClient;

	public void setUp() {
		if (sesClient == null) {
			sesClient = ConfigUtil.getSESClient();
		}
	}


	public void testListModels() throws SESException {
		logger.info("testListModels - entry");
		Collection<Model> models = sesClient.listModels();
		assertNotNull(models);
		logger.info("testListModels - exit");
	}


}

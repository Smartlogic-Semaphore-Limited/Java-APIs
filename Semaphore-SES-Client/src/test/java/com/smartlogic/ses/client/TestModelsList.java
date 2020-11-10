package com.smartlogic.ses.client;

import com.smartlogic.ses.client.exceptions.SESException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestModelsList extends PrintingTestCase {
	protected final Log logger = LogFactory.getLog(getClass());
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

	public void testListModels() throws SESException {
		logger.info("testListModels - entry");

		wireMockRule.stubFor(get(urlEqualTo("/ses?template=service.xml&service=modelslist"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody("<SEMAPHORE>\n" +
								"<MODELS>\n" +
								"<MODEL>\n" +
								"<NAME>IPSV</NAME>\n" +
								"<LANGUAGE>en</LANGUAGE>\n" +
								"</MODEL>\n" +
								"</MODELS>\n" +
								"</SEMAPHORE>")));
		Collection<Model> models = sesClient.listModels();
		assertNotNull(models);
		logger.info("testListModels - exit");
	}


}

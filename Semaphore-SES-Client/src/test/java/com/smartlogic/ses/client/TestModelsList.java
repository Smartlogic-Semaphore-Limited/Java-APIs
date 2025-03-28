package com.smartlogic.ses.client;

import com.smartlogic.ses.client.exceptions.SESException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestModelsList extends PrintingTestCase {
  private static SESClient sesClient;
  protected static final Logger logger = LoggerFactory.getLogger(TestModelsList.class);

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
        .willReturn(aResponse().withHeader("Content-Type", "text/xml")
            .withBody(readFileToString("src/test/resources/ses/sesResponseModelsList.xml"))));
    Collection<Model> models = sesClient.listModels();
    assertNotNull(models);
    logger.info("testListModels - exit");
  }

}

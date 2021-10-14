package com.smartlogic.ses.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TestModelStructure extends SESServerMockTestCase {

  private static SESClient sesClient;

  @Override
  public void setUp() {
    wireMockRule.start();
    if (sesClient == null) {
      sesClient = ConfigUtil.getSESClient();
    }
  }

  @Override
  public void tearDown() {
    wireMockRule.stop();
  }

  public void testIndexMetadata() throws Exception {
    wireMockRule.stubFor(
        get(urlEqualTo("/ses/IPSV")).willReturn(aResponse().withHeader("Content-Type", "text/xml")
            .withBody(readFileToString("src/test/resources/ses/sesResponseModelStructure.xml"))));

    OMStructure structure = sesClient.getStructure();
    IndexMetadata metas = structure.getIndexMetadata();

    OffsetDateTime d = OffsetDateTime.of(2021, 3, 18, 15, 40, 44, 8000000, ZoneOffset.UTC);

    assertEquals(metas.getPublishDate(), d);

  }
}

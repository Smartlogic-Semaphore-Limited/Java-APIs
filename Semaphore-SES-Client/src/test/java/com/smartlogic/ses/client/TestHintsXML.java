package com.smartlogic.ses.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.util.Map;


public class TestHintsXML extends SESServerMockTestCase {
  protected static final Logger logger = LoggerFactory.getLogger(TestHintsXML.class);
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

  public void testMarshal() throws Exception {
    wireMockRule.stubFor(
        get(urlEqualTo("/ses?TBDB=IPSV&template=service.xml&service=PREFIX&term_prefix=appo"))
            .willReturn(aResponse().withHeader("Content-Type", "text/xml")
                .withBody(readFileToString("src/test/resources/ses/sesResponsePrefixAppo.xml"))));

    Map<String, TermHint> termHints = sesClient.getTermHints("appo");

    XMLifier<TermHint> xmlifier = new XMLifier<>(TermHint.class);
    TermHint startTh = termHints.values().iterator().next();
    String savedXml = xmlifier.objectAsXML(startTh);
    TermHint newTh = xmlifier.objectFromXML(savedXml);

    assertEquals("Values arrays are not the same size", startTh.getValues().getValues().size(),
        newTh.getValues().getValues().size());
    assertEquals("Term IDs do not match", startTh.getId(), newTh.getId());
    assertEquals("Term names do not match", startTh.getName(), newTh.getName());
  }

  public void testUnmarshall() throws Exception {
    XMLifier<TermHint> xmlifier = new XMLifier<>(TermHint.class);
    TermHint termHint =
        xmlifier.objectFromXML(readFileToString("src/test/resources/ses/termHintSerialized.xml"));

    assertEquals("TermHint values", "intments procedure",
        termHint.getValues().getValues().get(0).getPostEm());
  }

}

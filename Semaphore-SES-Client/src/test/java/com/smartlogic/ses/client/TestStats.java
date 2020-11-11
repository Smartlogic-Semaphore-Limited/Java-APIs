package com.smartlogic.ses.client;

import com.smartlogic.ses.client.exceptions.SESException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestStats extends SESServerMockTestCase {
    protected final Log logger = LogFactory.getLog(getClass());
    private static SESClient sesClient;

    private static final String IPSV_MODEL_NAME = "IPSV";
    private static final Integer IPSV_MODEL_TERM_COUNT = 7917;

    public void setUp() throws Exception {
        wireMockRule.start();
        if (sesClient == null) {
            sesClient = ConfigUtil.getSESClient();
        }
    }

    public void tearDown() {
        wireMockRule.stop();
    }

    public void testStats() {

        wireMockRule.stubFor(get(urlEqualTo("/ses?template=service.xml&service=stats"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody(readFileToString("src/test/resources/ses/sesResponseStats.xml"))));

        try {
            StatisticsInfo info = sesClient.getStatistics();
            logger.info("Statistics: num indexes: " + info.getNumOfIndexes());
            logger.info("Statistics: num requests: " + info.getTotalNumOfRequests());
            for (Map.Entry<String, Integer> entry : info.getTermCounts().entrySet()) {
                logger.info("Index: " + entry.getKey() + ": " + entry.getValue());
            }

            assertEquals(info.getTermCount(IPSV_MODEL_NAME), IPSV_MODEL_TERM_COUNT.intValue());

        } catch (SESException sese) {
            logger.error(sese);
        }
    }
}

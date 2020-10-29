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
                        .withBody(statsResponseXml)));

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

    private static final String statsResponseXml = "" +
            "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n" +
            "<SEMAPHORE>\n" +
            "    <STATS indexes=\"1\" requests=\"3\">\n" +
            "        <Indexes>\n" +
            "            <NoIndex readAt=\"2020-10-29T17:05:09.929Z\" startAt=\"2020-10-29T17:01:55.958Z\">\n" +
            "                <Requests requests=\"3\">\n" +
            "                    <Languages>\n" +
            "                        <Language code=\"\" requests=\"3\"/>\n" +
            "                    </Languages>\n" +
            "                    <Commands>\n" +
            "                        <Command code=\"versions\" requests=\"1\">\n" +
            "                            <Performance>\n" +
            "                                <InProgress count=\"0\" longAverage=\"0\" longCount=\"0\" longest=\"0\" longestUrl=\"\"/>\n" +
            "                                <Completed average=\"0\" count=\"0\" inLast=\"5\" maximum=\"0\" minimum=\"0\" peakCount=\"0.0\"/>\n" +
            "                                <Completed average=\"0\" count=\"0\" inLast=\"60\" maximum=\"0\" minimum=\"0\" peakCount=\"0.0\"/>\n" +
            "                                <Completed average=\"91.3\" count=\"1\" inLast=\"600\" maximum=\"91\" minimum=\"91\" peakCount=\"0.3\"/>\n" +
            "                                <Completed average=\"91.3\" count=\"1\" inLast=\"3600\" maximum=\"91\" minimum=\"91\" peakCount=\"0.3\"/>\n" +
            "                                <Completed average=\"91.3\" count=\"1\" inLast=\"86400\" maximum=\"91\" minimum=\"91\" peakCount=\"0.3\"/>\n" +
            "                            </Performance>\n" +
            "                        </Command>\n" +
            "                        <Command code=\"stats\" requests=\"2\">\n" +
            "                            <Performance>\n" +
            "                                <InProgress count=\"1\" longAverage=\"0\" longCount=\"0\" longest=\"98\" longestUrl=\"\"/>\n" +
            "                                <Completed average=\"0\" count=\"0\" inLast=\"5\" maximum=\"0\" minimum=\"0\" peakCount=\"0.0\"/>\n" +
            "                                <Completed average=\"0\" count=\"0\" inLast=\"60\" maximum=\"0\" minimum=\"0\" peakCount=\"0.0\"/>\n" +
            "                                <Completed average=\"64.8\" count=\"1\" inLast=\"600\" maximum=\"65\" minimum=\"65\" peakCount=\"0.3\"/>\n" +
            "                                <Completed average=\"64.8\" count=\"1\" inLast=\"3600\" maximum=\"65\" minimum=\"65\" peakCount=\"0.3\"/>\n" +
            "                                <Completed average=\"64.8\" count=\"1\" inLast=\"86400\" maximum=\"65\" minimum=\"65\" peakCount=\"0.3\"/>\n" +
            "                            </Performance>\n" +
            "                        </Command>\n" +
            "                    </Commands>\n" +
            "                </Requests>\n" +
            "            </NoIndex>\n" +
            "            <Index name=\"IPSV\" readAt=\"2020-10-29T17:05:09.929Z\">\n" +
            "                <Labels>\n" +
            "                        \n" +
            "                    <ConceptSchemes>\n" +
            "                                \n" +
            "                        <Language code=\"en\" name=\"en\">\n" +
            "                                        \n" +
            "                            <Label count=\"16\" type=\"label\"/>\n" +
            "                                    \n" +
            "                        </Language>\n" +
            "                            \n" +
            "                    </ConceptSchemes>\n" +
            "                        \n" +
            "                    <Concepts count=\"3064\">\n" +
            "                                \n" +
            "                        <Language code=\"en\" name=\"en\">\n" +
            "                                        \n" +
            "                            <Label count=\"3064\" type=\"preferred label\" uri=\"http://www.w3.org/2008/05/skos-xl#prefLabel\"/>\n" +
            "                                        \n" +
            "                            <Label count=\"4837\" type=\"alternative label\" uri=\"http://www.w3.org/2008/05/skos-xl#altLabel\"/>\n" +
            "                                    \n" +
            "                        </Language>\n" +
            "                            \n" +
            "                    </Concepts>\n" +
            "                    \n" +
            "                </Labels>\n" +
            "            </Index>\n" +
            "        </Indexes>\n" +
            "    </STATS>\n" +
            "</SEMAPHORE>\n";
}

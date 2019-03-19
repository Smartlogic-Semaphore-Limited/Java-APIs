package com.smartlogic.ses.client;

import com.smartlogic.ses.client.exceptions.SESException;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class TestStats extends TestCase {
    protected final Log logger = LogFactory.getLog(getClass());
    private static SESClient sesClient;

    private static final String IPSV_MODEL_NAME = "IPSV";
    private static final Integer IPSV_MODEL_TERM_COUNT = 7917;

    public void setUp() throws Exception {
        if (sesClient == null) {
            sesClient = ConfigUtil.getSESClient();
        }
    }

    public void testStats() {

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

package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

public class TestAutoCloseable {

    @Test
    public void testBody() {
        try (ClassificationClient client = new ClassificationClient()) {
            ClassificationConfiguration config = new ClassificationConfiguration();
            config.setUrl("http://build-reference:5058");
            config.setSingleArticle(false);
            config.setMultiArticle(true);
            config.setSocketTimeoutMS(100000);
            config.setConnectionTimeoutMS(100000);
            client.setClassificationConfiguration(config);
            try {
                assertNotNull(client.getVersion());
            } catch (ClassificationException ce) {
                fail(ce.getMessage());
            }
        }
    }
}

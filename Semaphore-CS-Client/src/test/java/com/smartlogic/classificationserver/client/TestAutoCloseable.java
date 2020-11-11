package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

public class TestAutoCloseable extends ClassificationTestCase {

    @Test
    public void testAutoClose() {
        wireMockRule.stubFor(post(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/xml")
                        .withBody(readFileToString("src/test/resources/responses/csResponseGetVersion.xml"))));

        try (ClassificationClient client = new ClassificationClient()) {
            ClassificationConfiguration config = new ClassificationConfiguration();
            config.setUrl((String)this.config.get("cs.url"));
            if (this.config.containsKey("cs.proxyUrl")) {
                client.setProxyURL((String)this.config.get("cs.proxyUrl"));
            }
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

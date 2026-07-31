// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.cloud.TokenFetcher;
import org.junit.Assume;
import org.junit.Before;

/**
 * Base class for OE Client integration tests.
 *
 * <p>Connection details are read exclusively from environment variables or system properties so that no credentials
 * are ever committed to the (public) repository:
 *
 * <ul>
 *   <li>{@code OE_BASE_URL}   – required, e.g. {@code http://myserver:5080}</li>
 *   <li>{@code OE_TOKEN}      – optional static bearer token</li>
 *   <li>{@code OE_TOKEN_URL}  – optional cloud token endpoint (used together with OE_API_KEY)</li>
 *   <li>{@code OE_API_KEY}    – optional cloud API key</li>
 * </ul>
 *
 * If {@code OE_BASE_URL} is absent the test is skipped automatically.
 */
public abstract class AbstractOEIntegrationTest {

    protected static OEClientReadWrite oeClient;

    @Before
    public void setUpClient() throws CloudException {
        String baseUrl = System.getProperty("OE_BASE_URL");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = System.getenv("OE_BASE_URL");
        }

        Assume.assumeTrue("OE_BASE_URL not set — skipping integration tests", baseUrl != null && !baseUrl.isBlank());

        oeClient = new OEClientReadWrite();
        oeClient.setBaseURL(baseUrl);

        String token = System.getProperty("OE_TOKEN");
        if (token == null || token.isBlank()) {
            token = System.getenv("OE_TOKEN");
        }
        if (token != null && !token.isBlank()) {
            oeClient.setHeaderToken(token);
        }

        String tokenUrl = System.getProperty("OE_TOKEN_URL");
        if (tokenUrl == null || tokenUrl.isBlank()) {
            tokenUrl = System.getenv("OE_TOKEN_URL");
        }
        String apiKey = System.getProperty("OE_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("OE_API_KEY");
        }
        if (tokenUrl != null && !tokenUrl.isBlank() && apiKey != null && !apiKey.isBlank()) {
            TokenFetcher tokenFetcher = new TokenFetcher(tokenUrl, apiKey);
            oeClient.setCloudToken(tokenFetcher.getAccessToken());
        }
    }
}

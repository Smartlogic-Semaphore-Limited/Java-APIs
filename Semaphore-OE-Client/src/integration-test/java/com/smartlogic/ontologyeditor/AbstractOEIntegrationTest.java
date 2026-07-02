// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import com.smartlogic.cloud.CloudException;
import com.smartlogic.cloud.TokenFetcher;
import org.junit.Assume;
import org.junit.Before;

/**
 * Base class for OE Client integration tests.
 *
 * <p>Connection details are read exclusively from environment variables so that no credentials
 * are ever committed to the (public) repository:
 *
 * <ul>
 *   <li>{@code OE_BASE_URL}   – required, e.g. {@code http://myserver:5080}</li>
 *   <li>{@code OE_MODEL_URI}  – required, e.g. {@code model:MyModel}</li>
 *   <li>{@code OE_TOKEN}      – optional static bearer token</li>
 *   <li>{@code OE_TOKEN_URL}  – optional cloud token endpoint (used together with OE_API_KEY)</li>
 *   <li>{@code OE_API_KEY}    – optional cloud API key</li>
 * </ul>
 *
 * If {@code OE_BASE_URL} or {@code OE_MODEL_URI} are absent the test is skipped automatically.
 */
public abstract class AbstractOEIntegrationTest {

    protected static OEClientReadWrite oeClient;

    @Before
    public void setUpClient() throws CloudException {
        String baseUrl = System.getenv("OE_BASE_URL");
        String modelUri = System.getenv("OE_MODEL_URI");

        Assume.assumeTrue("OE_BASE_URL not set — skipping integration tests", baseUrl != null);
        Assume.assumeTrue("OE_MODEL_URI not set — skipping integration tests", modelUri != null);

        oeClient = new OEClientReadWrite();
        oeClient.setBaseURL(baseUrl);
        oeClient.setModelUri(modelUri);

        String token = System.getenv("OE_TOKEN");
        if (token != null && !token.isBlank()) {
            oeClient.setToken(token);
        }

        String tokenUrl = System.getenv("OE_TOKEN_URL");
        String apiKey = System.getenv("OE_API_KEY");
        if (tokenUrl != null && !tokenUrl.isBlank() && apiKey != null && !apiKey.isBlank()) {
            TokenFetcher tokenFetcher = new TokenFetcher(tokenUrl, apiKey);
            oeClient.setCloudToken(tokenFetcher.getAccessToken());
        }
    }
}

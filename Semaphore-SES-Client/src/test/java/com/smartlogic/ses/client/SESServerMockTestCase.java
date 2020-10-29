package com.smartlogic.ses.client;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import junit.framework.TestCase;
import org.junit.ClassRule;
import org.junit.Rule;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public abstract class SESServerMockTestCase extends TestCase {

	@ClassRule
	public static WireMockClassRule wireMockRule = new WireMockClassRule(options().port(9999));

	@Rule
	public WireMockClassRule instanceRule = wireMockRule;

}

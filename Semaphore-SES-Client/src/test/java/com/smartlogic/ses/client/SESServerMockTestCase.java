package com.smartlogic.ses.client;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import junit.framework.TestCase;
import org.junit.ClassRule;
import org.junit.Rule;
import wiremock.org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public abstract class SESServerMockTestCase extends TestCase {

	@ClassRule
	public static WireMockClassRule wireMockRule = new WireMockClassRule(options().port(9999));

	@Rule
	public WireMockClassRule instanceRule = wireMockRule;

	/**
	 * Utility method to read file to string
	 * @param path
	 * @return
	 */
	public static String readFileToString(String path) {
		try {
			return FileUtils.readFileToString(new File(path));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
}

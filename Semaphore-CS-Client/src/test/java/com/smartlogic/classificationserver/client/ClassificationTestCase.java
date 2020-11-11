package com.smartlogic.classificationserver.client;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.yaml.snakeyaml.Yaml;
import wiremock.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public abstract class ClassificationTestCase {
	protected static ClassificationClient classificationClient;
	protected static Map<String, Object> config;

	@ClassRule
	public static WireMockClassRule wireMockRule = new WireMockClassRule(options().port(9999));

	@Rule
	public WireMockClassRule instanceRule = wireMockRule;

	@SuppressWarnings("unchecked")
	public void initConfig() {
		if (config != null)
			return;

		try {
			Yaml yaml = new Yaml();
			try (InputStream inputStream = this.getClass()
					.getClassLoader()
					.getResourceAsStream("default.config.yml")) {
				config = (Map<String, Object>) yaml.load(inputStream);
			}
		} catch (Exception e) {
			System.err.println("failed to load config.");
			System.err.println(e.toString());
		}
	}

	@BeforeTest
	public void setUp() {
		wireMockRule.start();
		initConfig();
		if (classificationClient == null) {
			classificationClient = new ClassificationClient();
			if (config.containsKey("cs.proxyUrl")) {
				classificationClient.setProxyURL((String)config.get("cs.proxyUrl"));
			}

			ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
			classificationConfiguration.setUrl((String)config.get("cs.url"));
			classificationConfiguration.setSingleArticle(false);
			classificationConfiguration.setMultiArticle(true);
			classificationConfiguration.setSocketTimeoutMS(100000);
			classificationConfiguration.setConnectionTimeoutMS(100000);

			Map<String, String> additionalParameters = new HashMap<>();
			additionalParameters.put("threshold","1");
			additionalParameters.put("language","en1");
			classificationConfiguration.setAdditionalParameters(additionalParameters);
			classificationClient.setClassificationConfiguration(classificationConfiguration);
		}
	}

	@AfterTest
	public void tearDown() {
		wireMockRule.stop();
	}

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



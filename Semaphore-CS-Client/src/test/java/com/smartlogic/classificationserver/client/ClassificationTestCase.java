package com.smartlogic.classificationserver.client;

import org.testng.annotations.BeforeTest;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class ClassificationTestCase {
	protected static ClassificationClient classificationClient;
	protected static Map<String, Object> config;

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
}



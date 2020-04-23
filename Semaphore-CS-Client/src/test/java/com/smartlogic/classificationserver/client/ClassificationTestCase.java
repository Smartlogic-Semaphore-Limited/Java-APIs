package com.smartlogic.classificationserver.client;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeTest;

public abstract class ClassificationTestCase {
	protected static ClassificationClient classificationClient;

	@BeforeTest
	public void setUp() {

		if (classificationClient == null) {
			classificationClient = new ClassificationClient();
			classificationClient.setProxyURL("http://localhost:8888");

			ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
			classificationConfiguration.setUrl("http://svrka02:5058");
			classificationConfiguration.setSingleArticle(false);
			classificationConfiguration.setMultiArticle(true);
			classificationConfiguration.setSocketTimeoutMS(100000);
			classificationConfiguration.setConnectionTimeoutMS(100000);

			Map<String, String> additionalParameters = new HashMap<String, String>();
			additionalParameters.put("threshold","1");
			additionalParameters.put("language","en1");
			classificationConfiguration.setAdditionalParameters(additionalParameters);
			classificationClient.setClassificationConfiguration(classificationConfiguration);
		}
	}
}



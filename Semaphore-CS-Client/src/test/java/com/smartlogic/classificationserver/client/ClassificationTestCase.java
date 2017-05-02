package com.smartlogic.classificationserver.client;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public abstract class ClassificationTestCase extends TestCase {
	protected static ClassificationClient classificationClient;


	public void setUp() {

		if (classificationClient == null) {
			classificationClient = new ClassificationClient();
//			classificationClient.setProxyHost("localhost");
//			classificationClient.setProxyPort(8888);

			ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
			classificationConfiguration.setProtocol("http");
			classificationConfiguration.setHostName("mlhostdev02");
			classificationConfiguration.setHostPort(5058);
			classificationConfiguration.setHostPath("/index.html");
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



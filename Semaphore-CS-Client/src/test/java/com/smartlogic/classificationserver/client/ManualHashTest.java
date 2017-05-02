package com.smartlogic.classificationserver.client;

import java.util.HashMap;
import java.util.Map;

public class ManualHashTest {

	public static void main(String[] args) throws ClassificationException {
		ClassificationClient classificationClient = new ClassificationClient();

		ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
		classificationConfiguration.setProtocol("http");
		classificationConfiguration.setHostName("localhost");
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

		Result result = classificationClient.getClassifiedDocument(new Body("Wibble bot"), new Title("Cheese crackers"));
		System.out.println("HASH: '" + result.getHash() + "'");

	}

}

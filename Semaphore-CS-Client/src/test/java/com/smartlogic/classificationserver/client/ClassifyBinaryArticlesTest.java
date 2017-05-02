package com.smartlogic.classificationserver.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClassifyBinaryArticlesTest extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyBinaryArticlesTest.class);
	protected static ClassificationClient classificationClient;

	public void setUp() {

		if (classificationClient == null) {
			classificationClient = new ClassificationClient();

			ClassificationConfiguration classificationConfiguration = new ClassificationConfiguration();
			classificationConfiguration.setProtocol("http");
			classificationConfiguration.setHostName("mlhostdev02");
			classificationConfiguration.setHostPort(5058);
			classificationConfiguration.setHostPath("/index.html");
			classificationConfiguration.setSingleArticle(false);
			classificationConfiguration.setMultiArticle(true);
			classificationConfiguration.setFeedback(true);

			Map<String, String> additionalParameters = new HashMap<String, String>();
			additionalParameters.put("threshold","1");
			additionalParameters.put("language","en1");
			classificationConfiguration.setAdditionalParameters(additionalParameters);
			classificationClient.setClassificationConfiguration(classificationConfiguration);
		}
	}


	public void testBinary() throws IOException, ClassificationException {
		File file = new File("src/test/resources/data/44157109.pdf");
		FileInputStream fileInputStream = new FileInputStream(file);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int available;
		while ((available = fileInputStream.available()) > 0) {
			byte[] data = new byte[available];
			fileInputStream.read(data);
			byteArrayOutputStream.write(data);
		}
		fileInputStream.close();
		byteArrayOutputStream.close();

		Result result = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "44157109.pdf");
		assertEquals("Article count", 1, result.getArticles().size());

	}


}

package com.smartlogic.classificationserver.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

public class ClassifyBinaryArticlesTest extends ClassificationTestCase {
	protected static final Logger logger = LoggerFactory.getLogger(ClassifyBinaryArticlesTest.class);

	@Test
	public void testBinary() throws IOException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSampleData.xml"))));

		File file = new File("src/test/resources/data/SampleData.txt");
		try (FileInputStream fileInputStream = new FileInputStream(file);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {

			int available;
			while ((available = fileInputStream.available()) > 0) {
				byte[] data = new byte[available];
				int readCount = fileInputStream.read(data);
				if (readCount > 0) {
					byteArrayOutputStream.write(data);
				}
			}

			Result result = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(),
					"SampleData.txt");
			assertEquals(1, result.getArticles().size());
		}
	}

}

package com.smartlogic.classificationserver.client;

import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class ClassifyLargeTest extends ClassificationTestCase {

	@Test
	public void testBinary() throws IOException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSampleData.xml"))));

		URL log4jXmlUrl = ClassLoader.getSystemResource("log4j.xml");
		if (log4jXmlUrl != null)
			System.out.println(log4jXmlUrl.toExternalForm());
		File file = new File("src/test/resources/data/SampleData.txt");
		try (FileInputStream fileInputStream = new FileInputStream(file); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
			
			int available;
			while ((available = fileInputStream.available()) > 0) {
				byte[] data = new byte[available];
				int readBytes = fileInputStream.read(data);
				if (readBytes > 0) {
					byteArrayOutputStream.write(data);
				}
			}

			classificationClient.getClassificationConfiguration().setConnectionTimeoutMS(0);
			classificationClient.getClassificationConfiguration().setSocketTimeoutMS(0);

			try {
				Date startDate = new Date();
				classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt");
				Date endDate = new Date();
				assertTrue((endDate.getTime() - startDate.getTime()) < 100000,
						"Classification took too long: " + (endDate.getTime() - startDate.getTime()) + "ms");
			} catch (Exception e) {
				fail("ClassifyLargeTest failure: " + e.getMessage());
			}

		}
	}
}

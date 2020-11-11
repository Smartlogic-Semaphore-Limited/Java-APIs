package com.smartlogic.classificationserver.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.testng.Assert.assertEquals;

public class ClassifyDocumentWithHashTest extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyDocumentWithHashTest.class);

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
				int readBytes = fileInputStream.read(data);
				if (readBytes > 0) {
					byteArrayOutputStream.write(data);
				}
			}
			fileInputStream.close();
			byteArrayOutputStream.close();

			Result result1 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(),
					"SampleData.txt");
			assertEquals("ebb1736d30b446a6cba45923076a18fa", result1.getHash(), "Hash 1");

			wireMockRule.stubFor(post(urlEqualTo("/"))
					.willReturn(aResponse()
							.withHeader("Content-Type", "text/xml")
							.withBody(readFileToString("src/test/resources/responses/csResponseSampleDataWithMetas.xml"))));

			Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
			Collection<String> cheeses = new Vector<String>();
			cheeses.add("Brie");
			cheeses.add("Camenbert");
			cheeses.add("Cheddar");
			metadata.put("cheeses", cheeses);

			Result result2 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(),
					"SampleData.txt", new Title("title"), metadata);
			assertEquals("d946fd5c0b416c0b0ff56a3a1a94c9ad", result2.getHash(), "Hash 2");

		}
	}

}

package com.smartlogic.classificationserver.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class ClassifyBinaryTest extends ClassificationTestCase {
	protected static final Logger logger = LoggerFactory.getLogger(ClassifyBinaryTest.class);

	@Test
	public void testBinary() throws IOException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSampleData.xml"))));

		File file = new File("src/test/resources/data/SampleData.txt");
		try (FileInputStream fileInputStream = new FileInputStream(file); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();) {
			
			int available;
			while ((available = fileInputStream.available()) > 0) {
				byte[] data = new byte[available];
				int readCount = fileInputStream.read(data);
				if (readCount > 0) {
					byteArrayOutputStream.write(data);
				}
			}

			Map<String, Collection<ClassificationScore>> binaryScores1 = classificationClient
					.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt")
					.getAllClassifications();
			assertEquals(7, binaryScores1.size(), "run1 - Category count");
			assertEquals(2, binaryScores1.get("IPSV-Information and communication").size(), "run1 - IPSV-Information and communication");


			Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
			Collection<String> cheeses = new Vector<String>();
			cheeses.add("Brie");
			cheeses.add("Camenbert");
			cheeses.add("Cheddar");
			metadata.put("cheeses", cheeses);

			Map<String, Collection<ClassificationScore>> binaryScores2 = classificationClient
					.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt", new Title("title"),
							metadata)
					.getAllClassifications();
			assertEquals(7, binaryScores2.size(), "run2 - Category count");
			assertEquals(2, binaryScores2.get("IPSV-Information and communication").size(), "run2 - IPSV-Information and communication");
		}
	}
}

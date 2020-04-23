package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

public class ClassifyBinaryTest extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyBinaryTest.class);

	@Test
	public void testBinary() throws IOException, ClassificationException {
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
			assertEquals(6, binaryScores1.size(), "run1 - Category count");
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
			assertEquals(6, binaryScores1.size(), "run2 - Category count");
			assertEquals(2, binaryScores1.get("IPSV-Information and communication").size(), "run2 - IPSV-Information and communication");
		}
	}
}

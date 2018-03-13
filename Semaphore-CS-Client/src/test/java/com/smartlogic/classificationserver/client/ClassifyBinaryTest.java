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

		Map<String, Collection<ClassificationScore>> binaryScores1 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt").getAllClassifications();
		assertEquals(7, binaryScores1.get("IPSV").size(), "run 1 - IPSV");
		assertEquals(7, binaryScores1.get("IPSV_ID").size(), "run 1 - IPSV_ID");
		assertEquals(6, binaryScores1.get("IPSV_RAW").size(), "run 1 - IPSV_RAW");

		Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
		Collection<String> cheeses = new Vector<String>();
		cheeses.add("Brie");
		cheeses.add("Camenbert");
		cheeses.add("Cheddar");
		metadata.put("cheeses", cheeses);

		Map<String, Collection<ClassificationScore>> binaryScores2 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt", new Title("title"), metadata).getAllClassifications();
		assertEquals(7, binaryScores2.get("IPSV").size(), "run 2 - Generic");
		assertEquals(7, binaryScores2.get("IPSV_ID").size(), "run 2 - IPSV_ID");
		assertEquals(6, binaryScores2.get("IPSV_RAW").size(), "run 2 - IPSV_RAW");
	}
}

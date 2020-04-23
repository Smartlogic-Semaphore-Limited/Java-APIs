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

public class ClassifyDocumentWithHashTest extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyDocumentWithHashTest.class);

	@Test
	public void testBinary() throws IOException, ClassificationException {
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
			assertEquals("e1bd32ef968c3508c1cae0d909b33a84", result1.getHash(), "Hash 1");

			Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
			Collection<String> cheeses = new Vector<String>();
			cheeses.add("Brie");
			cheeses.add("Camenbert");
			cheeses.add("Cheddar");
			metadata.put("cheeses", cheeses);

			Result result2 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(),
					"SampleData.txt", new Title("title"), metadata);
			assertEquals("7067aac90aea369e3220eb74fb12596a", result2.getHash(), "Hash 2");

		}
	}

}

package com.smartlogic.classificationserver.client;

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

public class ClassifyDocumentWithHashTest extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyDocumentWithHashTest.class);


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

		Result result1 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt");
		assertEquals("Hash 1", "f7be152b1d057570b892dbe3dc39bd70", result1.getHash());

		Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
		Collection<String> cheeses = new Vector<String>();
		cheeses.add("Brie");
		cheeses.add("Camenbert");
		cheeses.add("Cheddar");
		metadata.put("cheeses", cheeses);

		Result result2 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt", new Title("title"), metadata);
		assertEquals("Hash 2", "c723555e774c94d0ead71d5c1cc06efc", result2.getHash());

	}


}

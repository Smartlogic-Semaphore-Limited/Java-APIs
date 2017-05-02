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

		Result result1 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "44157109.pdf");
		assertEquals("Hash 1", "7f7356fdef76a7687d87deba8b67f5ec", result1.getHash());

		Map<String, Collection<String>> metadata = new HashMap<String, Collection<String>>();
		Collection<String> cheeses = new Vector<String>();
		cheeses.add("Brie");
		cheeses.add("Camenbert");
		cheeses.add("Cheddar");
		metadata.put("cheeses", cheeses);

		Result result2 = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "44157109.pdf", new Title("title"), metadata);
		assertEquals("Hash 2", "45a70c845feca6de26c14ca9f9132282", result2.getHash());

	}


}

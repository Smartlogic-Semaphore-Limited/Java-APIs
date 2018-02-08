package com.smartlogic.classificationserver.client;

import static org.testng.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

public class ClassifyBinaryArticlesTest extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyBinaryArticlesTest.class);

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

		Result result = classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt");
		assertEquals(1, result.getArticles().size());
	}

}

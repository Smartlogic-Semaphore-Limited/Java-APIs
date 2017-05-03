package com.smartlogic.classificationserver.deprecated;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import com.smartlogic.classificationserver.client.ClassificationException;
import com.smartlogic.classificationserver.client.ClassificationTestCase;

public class ClassifyLarge extends ClassificationTestCase {
	@SuppressWarnings("deprecation")
	public void testBinary() throws IOException, ClassificationException {
		URL log4jUrl = ClassLoader.getSystemResource("log4j.properties");
		if (log4jUrl != null) System.out.println(log4jUrl.toExternalForm());

		URL log4jXmlUrl = ClassLoader.getSystemResource("log4j.xml");
		if (log4jXmlUrl != null) System.out.println(log4jXmlUrl.toExternalForm());
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

		classificationClient.getClassificationConfiguration().setConnectionTimeoutMS(0);
		classificationClient.getClassificationConfiguration().setSocketTimeoutMS(0);

		try {
			System.out.println("Starting  classification: " + new Date());
			classificationClient.classifyBinary(byteArrayOutputStream.toByteArray(), "SampleData.txt");
			System.out.println("Finished  classification: " + new Date());
		} catch (Exception e) {
			System.out.println("Exception classification: " + new Date());
		}

	}

}

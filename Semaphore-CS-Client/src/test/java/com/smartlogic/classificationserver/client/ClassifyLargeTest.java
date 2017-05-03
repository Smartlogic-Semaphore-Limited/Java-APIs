package com.smartlogic.classificationserver.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

public class ClassifyLargeTest extends ClassificationTestCase {
	public void testBinary() throws IOException, ClassificationException {

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
			Date startDate = new Date();
			classificationClient.getClassifiedDocument(byteArrayOutputStream.toByteArray(), "SampleData.txt");
			Date endDate = new Date();
			assertTrue("Classification took too long: " + (endDate.getTime() - startDate.getTime()) + "ms", (endDate.getTime() - startDate.getTime()) < 100000);
		} catch (Exception e) {
			fail("ClassifyLargeTest failure: " + e.getMessage());
		}

	}

}

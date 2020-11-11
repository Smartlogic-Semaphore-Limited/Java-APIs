package com.smartlogic.classificationserver.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ClassifyBinaryInXmlOut extends ClassificationTestCase {
	protected final static Log logger = LogFactory.getLog(ClassifyBinaryTest.class);

	@Test
	public void testBinary() throws IOException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSamplePdf.xml"))));

		File file = new File("src/test/resources/data/sample.pdf");
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
			byte[] xmlb = classificationClient.getClassificationServerResponse(byteArrayOutputStream.toByteArray(), "",
					null, null);
			assert xmlb != null;
			XMLReader.getDocument(xmlb);
		}
	}

	@Test
	public void testBodyInBytesOut() throws IOException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseSamplePdf.xml"))));

		File file = new File("src/test/resources/data/sample.pdf");
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
			byte[] xmlb = classificationClient.getClassificationServerResponse(byteArrayOutputStream.toByteArray(), "",
					null, null);
			assert xmlb != null;
			XMLReader.getDocument(xmlb);
		}
	}

	private Map<String, Collection<String>> makeMetaData(Properties props) {
		Map<String, Collection<String>> metadata = new HashMap<>();
		String[] metaflds = { "meta1", "meta2" };
		for (String mdf : metaflds) {
			String val = props.getProperty(mdf);
			if (val != null && val.length() > 0) {
				Collection<String> vals = new ArrayList<>();
				for (String v : val.split(","))
					vals.add(v);
				metadata.put(mdf, vals);
			}
		}
		return metadata;

	}

	@Test
	public void testBodyTitleMetaInBytesOut() throws IOException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseTestInBytesOut.xml"))));

		Properties props = new Properties();
		File file = new File("src/test/resources/data/testInBytesOut.txt");
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			props.load(fileInputStream);
			byte[] xmlb = classificationClient.getClassifiedBytes(new Body(props.getProperty("body")),
					new Title(props.getProperty("title")), makeMetaData(props));
			assert xmlb != null;
			XMLReader.getDocument(xmlb);
		}
	}

	@Test
	public void testUrlTitleMetaInBytesOut() throws IOException, ClassificationException {
		wireMockRule.stubFor(post(urlEqualTo("/"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "text/xml")
						.withBody(readFileToString("src/test/resources/responses/csResponseTestInBytesOut.xml"))));

		Properties props = new Properties();
		File file = new File("src/test/resources/data/testInBytesOut.txt");
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			props.load(fileInputStream);
			byte[] xmlb = classificationClient.getClassifiedBytes(new URL(props.getProperty("url")),
					new Title(props.getProperty("title")), makeMetaData(props));
			assert xmlb != null;
			XMLReader.getDocument(xmlb);
		}
	}
}
